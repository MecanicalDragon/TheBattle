package net.medrag.theBattle.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap


/**
 * {@author} Stanislav Tretyakov
 * 21.01.2020
 * SimpleAction handler
 */
@Service
class ActionService(@Autowired private val battleService: BattleService,
                    @Autowired private val attackService: AttackService,
                    @Autowired private val wSocket: SimpMessagingTemplate) {

    /**
     * SimpleAction handling
     * @param playerName String - action performer
     * @param bud UUID - battle UUID
     * @param simpleAction SimpleAction - action itself
     * @return ActionResult - result data object
     * @throws ValidationException if action unit is invalid or position is passed incorrectly
     * @throws ProcessingException if another player already acts.
     */
    @Throws(ValidationException::class, ProcessingException::class)
    fun performSimpleAction(playerName: String, bud: UUID, simpleAction: SimpleAction): ActionResult {
        val pair = battleService.getDislocations(playerName, bud)
        val player: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val foesSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        val actor = player.map[simpleAction.actor] as UnitDTO

        if (pair.actionInProcess.compareAndSet(false, true)) {
            try {
                if (actor === pair.actionMan || simpleAction.action === ActionType.CONCEDE) {
                    actor.effects.remove(UnitEffects.IN_BLOCK)
                    val additionalData = HashMap<String, Any>()
                    val comments = StringBuilder()
                    var battleWon = false
                    val nextUnit = when (simpleAction.action) {
                        ActionType.WAIT -> {
                            actionWait(actor, comments, pair)
                        }
                        ActionType.BLOCK -> {
                            actionBlock(actor, comments, pair)
                        }
                        ActionType.CONCEDE -> {
                            comments.append("$playerName concedes! ${foesSquad.playerName} wins the battle!")
                            finishTheBattle(bud, foesSquad, player, actor, true)
                            battleWon = true
                            pair.actionMan
                        }
                        ActionType.ATTACK -> {

                            val targets = simpleAction.additionalData["targets"] as List<*>
                            val targetsPositions = targets.map {
                                try {
                                    Position.valueOf(it.toString().toUpperCase())
                                } catch (e: Exception) {
                                    val error = "Unit position $it has been passed incorrectly for player ${playerName}!"
                                    logger.error(error)
                                    throw ValidationException(error)
                                }
                            }

                            //  Validate attack
                            if (actor.type.distance === Unitt.Unit.Distance.CLOSED) {
                                if (!validateAttack(simpleAction.actor, targetsPositions, player, foesSquad))
                                    throw ValidationException("Attack of ${simpleAction.actor} to $targetsPositions is not valid in current conditions!")
                            }

                            comments.append("${actor.name} attacks! ")
                            var accuracy = actor.type.accuracy + ACCURACY_MODIFIER
                            var targetDied = false;
                            val attackPower = actor.type.attack
                            if (targets.size > 1) comments.append("\n")
                            targetsPositions.forEach {
                                val unit = foesSquad.map[it] as UnitDTO
                                if (actor.type.distance === Unitt.Unit.Distance.RANGED)
                                    accuracy = attackService.calculateAccuracy(accuracy, simpleAction.actor, player.type)
                                val result = attackService.sufferDamage(unit, accuracy, attackPower)
                                comments.append(result.first)
                                if (result.second) targetDied = true
                            }
                            additionalData[DAMAGED_SQUAD] = foesSquad;
                            if (targetDied && foesSquad.map.values.none { it.hp > 0 }) {
                                finishTheBattle(bud, player, foesSquad, actor, false)
                                battleWon = true
                                comments.append("\n$playerName wins the battle!")
                                pair.actionMan
                            } else {
                                pair.makeMove()
                            }
                        }
                    }
                    val actionResult = ActionResult(simpleAction.action, additionalData, nextUnit, pair.lastMove,
                            comments.toString(), battleWon)
                    wSocket.convertAndSend("/battle/${foesSquad.playerName}",
                            ObjectMapper().writeValueAsString(actionResult))
                    return actionResult
                } else {
                    val warn = "Unit ${actor.name} tries to make a move in another's turn with player $playerName!"
                    logger.warn(warn)
                    throw ValidationException(warn)
                }
            } finally {
                pair.actionInProcess.set(false)
            }
        } else throw ProcessingException("Another player already acts.")
    }

    /**
     * Shifts turn to next unit if turn time exceeded.
     * If it's possible, applies to current unit {@link ActionType.WAIT}. If not - just passes.
     * Result is passed by websocket.
     * @param playerName String
     * @param bud UUID
     */
    fun pingTurn(playerName: String, bud: UUID) {
        val pair = battleService.getDislocations(playerName, bud)
        if (pair.lastMove < System.currentTimeMillis() - TURN_TIME) {
            if (pair.actionInProcess.compareAndSet(false, true)) {
                try {
                    if (pair.lastMove < System.currentTimeMillis() - TURN_TIME) {
                        val comments = StringBuilder()
                        var actionMan = pair.actionMan
                        if (actionMan.initiative > INITIATIVE_BOTTOM_THRESHOLD) {
                            actionMan = actionWait(actionMan, comments, pair)
                        } else {
                            comments.append("${pair.actionMan.name} waits for something, but time is not!\n")
                            actionMan = pair.makeMove()
                            comments.append("Now it's ${actionMan.name}'s turn!")
                        }
                        val actionResult = ActionResult(ActionType.WAIT, null, actionMan, pair.lastMove,
                                comments.toString())
                        wSocket.convertAndSend("/battle/${pair.foe1.playerName}",
                                ObjectMapper().writeValueAsString(actionResult))
                        wSocket.convertAndSend("/battle/${pair.foe2.playerName}",
                                ObjectMapper().writeValueAsString(actionResult))
                    }
                } finally {
                    pair.actionInProcess.set(false)
                }
            }
        }
    }

    /**
     * Validate attack for closed-range unit.
     * @param realActor Position
     * @param targets List<Position>
     * @param playerSquad ValidatedSquad
     * @param foesSquad ValidatedSquad
     * @return Boolean
     */
    private fun validateAttack(realActor: Position, targets: List<Position>, playerSquad: ValidatedSquad, foesSquad: ValidatedSquad): Boolean {

        //TODO: now we just mock validation
        if (Math.random() < 1) return true
        else {
            println(targets)
        }

        class Targets {
            var pos1 = false
            var pos2 = false
            var pos3 = false
            var pos4 = false
            var pos5 = false
        }

//        val t = Array(5) { false }

        var actor: Position = realActor

        val t = Targets()
        if (playerSquad.type === SquadType.FORCED_FRONT) {

            // If attacker is in the rear line
            if (actor == Position.POS2 || actor == Position.POS4) {
                if (playerSquad.pos3.hp == 0) {
                    if (actor === Position.POS2 && playerSquad.pos1.hp == 0) {
                        actor = Position.POS3
                    } else if (actor === Position.POS4 && playerSquad.pos5.hp == 0) {
                        actor = Position.POS3
                    } else return false
                } else return false
            }

            if (foesSquad.type === SquadType.FORCED_FRONT) {

                // Basic validation
                if (playerSquad.pos3.hp == 0) {
                    t.pos1 = true
                    t.pos3 = true
                    t.pos5 = true
                } else {
                    when (actor) {
                        Position.POS1 -> {
                            t.pos1 = true
                            t.pos3 = true
                        }
                        Position.POS3 -> {
                            t.pos1 = true
                            t.pos3 = true
                            t.pos5 = true
                        }
                        Position.POS5 -> {
                            t.pos3 = true
                            t.pos5 = true
                        }
                        else -> {
                        }
                    }
                }

                //  If enemy's front line is dead
                if (foesSquad.pos3.hp == 0) {
                    if (foesSquad.pos1.hp == 0 && (foesSquad.pos5.hp == 0 || actor !== Position.POS5)) t.pos2 = true
                    if (foesSquad.pos5.hp == 0 && (foesSquad.pos1.hp == 0 || actor !== Position.POS1)) t.pos4 = true
                }

            }
            //  foesSquad.type === SquadType.FORCED_BACK
            else {

                if (playerSquad.pos3.hp == 0) {
                    actor = Position.POS3
                }

                when (actor) {
                    Position.POS1 -> {
                        t.pos2 = true
                    }
                    Position.POS3 -> {
                        t.pos2 = true
                        t.pos4 = true
                    }
                    Position.POS5 -> {
                        t.pos4 = true
                    }
                    else -> {
                    }
                }

                //  If enemy's front line is dead
                val pos4dead = foesSquad.pos4.hp == 0
                val pos2dead = foesSquad.pos2.hp == 0
                if (pos4dead && pos2dead) {
                    t.pos3 = true
                    t.pos1 = true
                    t.pos5 = true
                } else {
                    if (pos2dead && realActor != Position.POS5) t.pos1 = true
                    if (pos4dead && realActor != Position.POS1) t.pos5 = true
                }

            }
        } else {    //  playerSquad.type === SquadType.FORCED_BACK

            var posX = false

            // If attacker is in the rear line
            when (actor) {
                Position.POS1, Position.POS3, Position.POS5 -> {
                    if (actor === Position.POS1 && playerSquad.pos2.hp == 0) actor = Position.POS2
                    else if (actor === Position.POS5 && playerSquad.pos4.hp == 0) actor = Position.POS4
                    else if (actor === Position.POS3 && playerSquad.pos2.hp == 0 && playerSquad.pos4.hp == 0) actor = Position.POS2 //TODO: variants
                    else return false
                }
                else -> {
                }
            }

            if (foesSquad.type === SquadType.FORCED_FRONT) {
                when (actor) {
                    Position.POS2 -> {
                        t.pos1 = true
                        t.pos3 = true
                        if (playerSquad.pos4.hp == 0) t.pos5 = true
                    }
                    Position.POS4 -> {
                        t.pos3 = true
                        t.pos5 = true
                        if (playerSquad.pos2.hp == 0) t.pos1 = true
                    }
                    else -> {
                    }
                }

                // If target is in the rear line
                if (foesSquad.pos3.hp == 0) {
                    if (foesSquad.pos1.hp == 0) {
                        if (posX || actor == Position.POS2 || playerSquad.pos2.hp == 0)
                            t.pos2 = true
                    }
                    if (foesSquad.pos5.hp == 0) {
                        if (posX || actor == Position.POS4 || playerSquad.pos4.hp == 0)
                            t.pos4 = true
                    }
                }
            } else {    //  foesSquad.type === SquadType.FORCED_BACKt.pos2 = true
                t.pos2 = true
                t.pos4 = true
            }
        }

        return true;
    }

    /**
     * Includes action BLOCK instructions
     * @param actor UnitDTO
     * @param comments StringBuilder
     * @param pair FoesPair
     * @return UnitDTO
     */
    private fun actionBlock(actor: UnitDTO, comments: StringBuilder, pair: FoesPair): UnitDTO {
        actor.effects.add(UnitEffects.IN_BLOCK)
        comments.append("${actor.name} goes into defence.")
        return pair.makeMove()
    }

    /**
     * Includes action WAIT instructions
     * @param actor UnitDTO
     * @param comments StringBuilder
     * @param pair FoesPair
     * @return UnitDTO
     */
    private fun actionWait(actor: UnitDTO, comments: StringBuilder, pair: FoesPair): UnitDTO {
        return if (actor.initiative > INITIATIVE_BOTTOM_THRESHOLD) {
            comments.append("${actor.name} waits for a moment...")
            pair.recalculateOrder()
        } else {
            comments.append("${actor.name} can not wait anymore! It's high time for action!")
            pair.actionMan
        }
    }

    /**
     * Just launch coroutine job to finish the battle
     * @param bud UUID - battle UUID
     * @param winner ValidatedSquad
     * @param looser ValidatedSquad
     * @param actor UnitDTO
     * @param conceded Boolean
     */
    private fun finishTheBattle(bud: UUID, winner: ValidatedSquad, looser: ValidatedSquad, actor: UnitDTO, conceded: Boolean) {
        GlobalScope.launch {
            var finished = false
            while (!finished) {
                try {
                    battleService.finishTheBattle(bud, winner, looser, actor, conceded)
                    finished = true
                } catch (e: Exception) {
                    logger.error("Database transaction has failed.")
                    logger.error(e.message)
                    delay(300)
                }
            }
        }
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}