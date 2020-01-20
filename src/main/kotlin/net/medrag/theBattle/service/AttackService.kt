package net.medrag.theBattle.service

import net.medrag.theBattle.model.ACCURACY_MODIFIER
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.ValidatedSquad
import net.medrag.theBattle.model.dto.ActionResult
import net.medrag.theBattle.model.dto.AttackAction
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.squad.FoesPair
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class AttackService(private val battleService: BattleService) {

    fun performAttack(playerName: String, bud: UUID, attackAction: AttackAction): ActionResult {
        val pair = battleService.getDislocations(playerName, bud)
        val attackersSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val sufferingSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        //TODO: validate attack order
        //TODO: validate attack
        val attacker: UnitDTO = attackersSquad.map[attackAction.attacker] as UnitDTO
        val accuracy = attacker.type.accuracy + ACCURACY_MODIFIER
        val attackPower = attacker.type.attack

        attackAction.targets.forEach {
            val unit = sufferingSquad.map[it] as UnitDTO
            sufferDamage(unit, accuracy, attackPower)
        }
        val nextTurnUnit = pair.makeMove()
//        val sufferedSquad = ValidatedSquad.sufferDamage(sufferingSquad, damagedMap)
//        val updatedPair = FoesPair(attackersSquad, sufferedSquad)
//        battleService.updateDislocations(playerName, bud, updatedPair)

        //TODO: trigger other player's websocket
        return ActionResult("ATTACK", sufferingSquad, nextTurnUnit)
    }

    private fun sufferDamage(unit: UnitDTO, accuracy: Int, attackPower: Int) {
        if (Math.random() * accuracy > unit.type.evasion) {
            //TODO: randomize damage
            val damage = attackPower - unit.type.defence
            var hp = unit.type.health - damage
            if (hp < 0) hp = 0
            unit.hp = hp
        }
    }

//    private fun mapPosition(position: String, squad: ValidatedSquad): UnitDTO = when (position) {
//        "pos1" -> squad.pos1
//        "pos2" -> squad.pos2
//        "pos3" -> squad.pos3
//        "pos4" -> squad.pos4
//        "pos5" -> squad.pos5
//        else -> throw ValidationException("wtf? Unit position cannot be $position")
//    }
}