package net.medrag.theBattle.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.classes.Ranger
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
     */
    @Throws(ValidationException::class)
    fun performSimpleAction(playerName: String, bud: UUID, simpleAction: SimpleAction): ActionResult {
        val pair = battleService.getDislocations(playerName, bud)
        val player: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val foesSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        val actor = player.map[simpleAction.actor] as UnitDTO
        actor.effects.remove(UnitEffects.IN_BLOCK)

        if (actor === pair.actionMan) {
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
                ActionType.ATTACK -> {

                    //TODO: validate attack

                    comments.append("${actor.name} attacks! ")
                    var accuracy = actor.type.accuracy + ACCURACY_MODIFIER
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
                            val result = attackService.sufferDamage(unit, accuracy, attackPower, foesSquad)
                            comments.append(result)
                        }
                    }
                    additionalData[DAMAGED_SQUAD] = foesSquad;
                    if (foesSquad.dead == 5) {
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
                        pair.actionMan
                    } else {
                        pair.makeMove()
                    }
                }
            }
            if (battleWon) {
                comments.append("\n$playerName wins the battle!")
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
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}