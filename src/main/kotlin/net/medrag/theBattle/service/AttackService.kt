package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.UnitEffects
import net.medrag.theBattle.model.squad.SquadType
import org.springframework.stereotype.Service
import kotlin.math.roundToInt
import net.medrag.theBattle.model.dto.Position.*

@Service
class AttackService {

    /**
     * Deal damage to target
     *
     * @param target UnitDTO
     * @param accuracy Int
     * @param attackPower Int
     * @return Pair<String, Boolean> - battle logs and boolean of 'is target died'
     */
    fun sufferDamage(target: UnitDTO, accuracy: Int, attackPower: Int): Pair<String, Boolean> {
        if (Math.random() * accuracy > target.type.evasion) {
            val inDefence = target.effects.contains(UnitEffects.IN_BLOCK)
            val comments: StringBuilder = StringBuilder(if (inDefence) "${target.name} in defence" else target.name)

            val defence = if (inDefence) {
                if (target.type.defence == 0) 2 else target.type.defence * 2
            } else target.type.defence
            val randomizedPower = randomizeDamage(attackPower)
            val damage = randomizedPower - defence
            if (damage > 0) {
                var hp = target.hp - damage
                if (hp < 0) hp = 0
                target.hp = hp
            }
            comments.append(" receives $damage of damage")
            if (target.hp == 0)
                comments.append(" and dies")
            comments.append(".")
            return Pair(comments.toString(), target.hp == 0)
        } else {
            return Pair("${target.name} dodges the attack!", false)
        }
    }

    /**
     * Validates if range unit attacks from a front line and reduces it's accuracy in this case
     *
     * @param accuracy Int unit's accuracy
     * @param pos Position - unit's position
     * @param squadType SquadType - attacker's squad type
     * @return Int - current accuracy
     */
    fun calculateAccuracy(accuracy: Int, pos: Position, squadType: SquadType): Int =
            if ((squadType === SquadType.FORCED_FRONT && (pos === POS1 || pos === POS2 || pos === POS3))
                    || (squadType === SquadType.FORCED_BACK && (pos === POS2 || pos === POS4))) {
                accuracy / 2
            } else accuracy

    private fun randomizeDamage(damage: Int): Int {
        val minDmg: Int = damage * 100 - damage * 20
        val random = minDmg + Math.random() * damage * 40
        return (random / 100).roundToInt()
    }
}