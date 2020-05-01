package net.medrag.theBattle.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap


/**
 * @author Stanislav Tretyakov
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
     * @throws ValidationException if:
     *          - action unit is dead
     *          - other unit's turn
     *          - attack is invalid
     *          - position is passed incorrectly
     *          - position data is invalid
     * @throws ProcessingException if another player already acts.
     */
    @Throws(ValidationException::class, ProcessingException::class)
    fun performSimpleAction(playerName: String, bud: UUID, simpleAction: SimpleAction): ActionResult {
        val pair = battleService.getDislocations(playerName, bud)
        val player: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val foesSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        val actor = player.map[simpleAction.actor] as UnitDTO
        if (actor.isDead() && simpleAction.action !== ActionType.CONCEDE)
            throw ValidationException("${simpleAction.actor} is dead. He can not perform any actions.")

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

                            val targets: List<Position> = simpleAction.additionalData["targets"]?.let {
                                if (it is List<*>) {
                                    it.map { pos ->
                                        try {
                                            Position.valueOf(pos.toString().toUpperCase())
                                        } catch (e: IllegalArgumentException) {
                                            throw ValidationException(TARGET_DATA_INVALID)
                                        }
                                    }
                                } else throw ValidationException(TARGET_DATA_INVALID)
                            } ?: throw ValidationException(TARGET_DATA_INVALID)
                            if (targets.isEmpty()) throw ValidationException(TARGET_DATA_INVALID)

                            //  Validate attack
                            if (actor.type.distance === Unitt.Unit.Distance.CLOSED) {
                                attackService.validateMeleeAttack(simpleAction.actor, targets, player, foesSquad)
                            }

                            comments.append("${actor.name} attacks! ")
                            var accuracy = actor.type.accuracy + ACCURACY_MODIFIER
                            var targetDied = false;
                            val attackPower = actor.type.attack
                            if (targets.size > 1) comments.append("\n")
                            targets.forEach {
                                val unit = foesSquad.map[it] as UnitDTO
                                if (unit.isAlive()) {
                                    if (actor.type.distance === Unitt.Unit.Distance.RANGED)
                                        accuracy = attackService.calculateAccuracy(accuracy, simpleAction.actor, player.type)
                                    comments.append(attackService.sufferDamage(unit, accuracy, attackPower))
                                    if (unit.isDead()) targetDied = true
                                }
                            }
                            additionalData[DAMAGED_SQUAD] = foesSquad;
                            if (targetDied && foesSquad.map.values.none { it.isAlive() }) {
                                finishTheBattle(bud, player, foesSquad, actor, false)
                                battleWon = true
                                comments.append("\n$playerName wins the battle!")
                                pair.actionMan
                            } else {
                                pair.makeMove()
                            }
                        }
                    }
                    val actionResult = ActionResult(simpleAction.action, nextUnit, pair.lastMove,
                            comments.toString(), additionalData, battleWon)
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
                        val actionResult = ActionResult(ActionType.WAIT, actionMan, pair.lastMove, comments.toString())
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