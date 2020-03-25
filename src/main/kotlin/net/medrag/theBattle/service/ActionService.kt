package net.medrag.theBattle.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.lang.Exception
import java.lang.StringBuilder
import java.time.LocalTime
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
                            if (actor.initiative > INITIATIVE_BOTTOM_THRESHOLD) {
                                comments.append("${actor.name} waits for a moment...")
                                pair.recalculateOrder()
                            } else {
                                comments.append("${actor.name} can not wait anymore! It's high time for action!")
                                pair.actionMan
                            }
                        }
                        ActionType.BLOCK -> {
                            actor.effects.add(UnitEffects.IN_BLOCK)
                            comments.append("${actor.name} goes into defence.")
                            pair.makeMove()
                        }
                        ActionType.CONCEDE -> {
                            comments.append("$playerName concedes! ${foesSquad.playerName} wins the battle!")
                            GlobalScope.launch {
                                var finished = false
                                while (!finished) {
                                    try {
                                        battleService.finishTheBattle(bud, foesSquad, player, actor, true)
                                        finished = true
                                    } catch (e: Exception) {
                                        logger.error("Database transaction has failed.")
                                        logger.error(e.message)
                                        delay(300)
                                    }
                                }
                            }
                            battleWon = true
                            pair.actionMan
                        }
                        ActionType.ATTACK -> {

                            //TODO: validate attack

                            comments.append("${actor.name} attacks! ")
                            var accuracy = actor.type.accuracy + ACCURACY_MODIFIER
                            var targetDied = false;
                            val attackPower = actor.type.attack
                            val targets = simpleAction.additionalData["targets"] as List<*>
                            if (targets.size > 1) comments.append("\n")
                            targets.forEach {
                                val pos = it.toString().toUpperCase();
                                if (!pos.matches(Regex("^POS[1-5]\$"))) {
                                    val error = "Unit position $pos has been passed incorrectly for player ${playerName}!"
                                    logger.error(error)
                                    throw ValidationException(error)
                                } else {
                                    val position = Position.valueOf(pos)
                                    val unit = foesSquad.map[position] as UnitDTO
                                    if (actor.type.distance === Unitt.Unit.Distance.RANGED)
                                        accuracy = attackService.calculateAccuracy(accuracy, simpleAction.actor, pos, player, foesSquad)
                                    val result = attackService.sufferDamage(unit, accuracy, attackPower)
                                    comments.append(result.first)
                                    if (result.second) targetDied = true
                                }
                            }
                            additionalData[DAMAGED_SQUAD] = foesSquad;
                            if (targetDied && foesSquad.map.values.none { it.hp > 0 }) {
                                GlobalScope.launch {
                                    var finished = false
                                    while (!finished) {
                                        try {
                                            battleService.finishTheBattle(bud, player, foesSquad, actor)
                                            finished = true
                                        } catch (e: Exception) {
                                            logger.error("Database transaction has failed.")
                                            logger.error(e.message)
                                            delay(300)
                                        }
                                    }
                                }
                                battleWon = true
                                comments.append("\n$playerName wins the battle!")
                                pair.actionMan
                            } else {
                                pair.makeMove()
                            }
                        }
                    }
                    val actionResult = ActionResult(simpleAction.action, additionalData, nextUnit, comments.toString(), battleWon)
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

    fun pingTurn(playerName: String, bud: UUID) {
        val pair = battleService.getDislocations(playerName, bud)
        if (pair.lastMove.isBefore(LocalTime.now().minusSeconds(TURN_TIME))) {
            if (pair.actionInProcess.compareAndSet(false, true)) {
                try {
                    if (pair.lastMove.isBefore(LocalTime.now().minusSeconds(TURN_TIME))) {
                        var comments = "${pair.actionMan.name} waits for something, but time is not! "
                        val next = pair.makeMove()
                        comments = comments.plus("Now it's ${next.name}'s turn!")
                        val actionResult = ActionResult(ActionType.WAIT, null, next, comments)
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

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}