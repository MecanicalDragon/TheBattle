package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.*
import org.springframework.stereotype.Service

@Service
class AttackService {

    fun sufferDamage(unit: UnitDTO, accuracy: Int, attackPower: Int) {
        if (Math.random() * accuracy > unit.type.evasion) {
            val defence = if (unit.effects.contains(UnitEffects.IN_BLOCK)) {
                if (unit.type.defence == 0) 2 else unit.type.defence * 2
            } else unit.type.defence
            //TODO: randomize damage
            val damage = attackPower - defence
            println("damage")
            println(damage)
            if (damage > 0) {
                var hp = unit.hp - damage
                if (hp < 0) hp = 0
                unit.hp = hp
            }
        } else {
            println("miss")
        }
    }
}