package net.medrag.theBattle.service

import com.fasterxml.jackson.databind.ObjectMapper
import net.medrag.theBattle.model.ACCURACY_MODIFIER
import net.medrag.theBattle.model.DAMAGED_SQUAD
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap


/**
 * {@author} Stanislav Tretyakov
 * 21.01.2020
 */
@Service
class ActionService(@Autowired private val battleService: BattleService,
                    @Autowired private val attackService: AttackService,
                    @Autowired private val wSocket: SimpMessagingTemplate) {

    fun performSimpleAction(playerName: String, bud: UUID, simpleAction: SimpleAction): ActionResult {
        val pair = battleService.getDislocations(playerName, bud)
        val player: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val foesSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        val actor = player.map[simpleAction.actor] as UnitDTO

        if (actor === pair.actionMan) {
            val additionalData = HashMap<String, Any>()
            val nextUnit = when (simpleAction.action) {
                ActionType.WAIT -> {
                    actor.initiative = actor.initiative / 3 * 2
                    actor.effects.remove(UnitEffects.IN_BLOCK)
                    pair.recalculateOrder()
                }
                ActionType.BLOCK -> {
                    actor.effects.add(UnitEffects.IN_BLOCK)
                    pair.makeMove()
                }
                ActionType.ATTACK -> {

                    //TODO: validate attack

                    actor.effects.remove(UnitEffects.IN_BLOCK)
                    val accuracy = actor.type.accuracy + ACCURACY_MODIFIER
                    val attackPower = actor.type.attack
                    val targets = simpleAction.additionalData["targets"] as List<*>
                    targets.forEach {
                        val pos = it.toString().toUpperCase();
                        if (!pos.matches(Regex("^POS[1-5]\$"))) {
                            logger.error("Unit position has been passed incorrectly!")
                            throw ValidationException("Unit position has been passed incorrectly!")
                        } else {
                            val position = Position.valueOf(pos)
                            val unit = foesSquad.map[position] as UnitDTO
                            attackService.sufferDamage(unit, accuracy, attackPower)
                        }
                    }
                    additionalData[DAMAGED_SQUAD] = foesSquad;
                    pair.makeMove()
                }
            }
            val actionResult = ActionResult(simpleAction.action, additionalData, nextUnit)
            wSocket.convertAndSend("/battle/${foesSquad.playerName}",
                    ObjectMapper().writeValueAsString(actionResult))
            return actionResult
        } else {
            val warn = "Unit tries to make a move in another's turn!"
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