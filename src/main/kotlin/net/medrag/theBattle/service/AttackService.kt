package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.UnitEffects
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class AttackService {

    fun sufferDamage(unit: UnitDTO, accuracy: Int, attackPower: Int, squad: ValidatedSquad): String {
        if (Math.random() * accuracy > unit.type.evasion) {
            val inDefence = unit.effects.contains(UnitEffects.IN_BLOCK)
            val comments: StringBuilder = StringBuilder(if (inDefence) "${unit.name} in defence" else unit.name)

            val defence = if (inDefence) {
                if (unit.type.defence == 0) 2 else unit.type.defence * 2
            } else unit.type.defence
            val randomizedPower = randomizeDamage(attackPower)
            val damage = randomizedPower - defence
            if (damage > 0) {
                var hp = unit.hp - damage
                if (hp < 0) hp = 0
                unit.hp = hp
            }
            comments.append(" receives $damage of damage")
            if (unit.hp == 0) {
                comments.append(" and dies")
                squad.dead++
            }
            comments.append(".")
            return comments.toString()
        } else {
            return "${unit.name} dodges the attack!"
        }
    }

    private fun randomizeDamage(damage: Int): Int {
        val minDmg: Int = damage * 100 - damage * 20
        val random = minDmg + Math.random() * damage * 40
        return (random / 100).roundToInt()
    }
}