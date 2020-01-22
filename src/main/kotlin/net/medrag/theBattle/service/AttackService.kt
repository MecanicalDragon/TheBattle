package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.*
import org.springframework.stereotype.Service

@Service
class AttackService {

    fun sufferDamage(unit: UnitDTO, accuracy: Int, attackPower: Int): String {
        if (Math.random() * accuracy > unit.type.evasion) {
            val inDefence = unit.effects.contains(UnitEffects.IN_BLOCK)
            val comments: StringBuilder = StringBuilder(if (inDefence) "${unit.name} in defence" else unit.name)

            val defence = if (inDefence) {
                if (unit.type.defence == 0) 2 else unit.type.defence * 2
            } else unit.type.defence

            //TODO: randomize damage

            val damage = attackPower - defence
            if (damage > 0) {
                var hp = unit.hp - damage
                if (hp < 0) hp = 0
                unit.hp = hp
            }
            comments.append(" receives $damage of damage")
            if (unit.hp == 0) comments.append(" and dies")
            comments.append(".")
            return comments.toString()
        } else {
            return "${unit.name} dodges the attack!"
        }
    }
}