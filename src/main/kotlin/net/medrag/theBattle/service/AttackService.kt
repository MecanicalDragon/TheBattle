package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.UnitEffects
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class AttackService {

    fun sufferDamage(target: UnitDTO, accuracy: Int, attackPower: Int, squad: ValidatedSquad): String {
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
            if (target.hp == 0) {
                comments.append(" and dies")
                squad.dead++
            }
            comments.append(".")
            return comments.toString()
        } else {
            return "${target.name} dodges the attack!"
        }
    }

    fun calculateAccuracy(accuracy: Int, atkPosition: Position, targetPosition: String,
                          attackerSquad: ValidatedSquad, sufferingSquad: ValidatedSquad): Int {
        val atkP = atkPosition.toString().substring(3).toInt()
        var reduced = false;
        if (attackerSquad.type === SquadType.FORCED_FRONT && atkP % 2 == 1) {
            if (sufferingSquad.type === SquadType.FORCED_FRONT) {
                if (sufferingSquad.pos3.hp != 0) {
                    reduced = true;
                } else if ((atkPosition === Position.POS1 && sufferingSquad.pos1.hp != 0)
                        || (atkPosition === Position.POS5 && sufferingSquad.pos5.hp != 0)) {
                    reduced = true;
                }
            } else {    //  FF vs FB
                if ((sufferingSquad.pos2.hp != 0 && atkP != 5) || (sufferingSquad.pos4.hp != 0 && atkP != 1)) reduced = true;
            }
            reduced = true
        } else if (attackerSquad.type === SquadType.FORCED_BACK && atkP % 2 == 0) {    //  FORCED_BACK
            if (sufferingSquad.type === SquadType.FORCED_BACK) {
                if (sufferingSquad.pos2.hp != 0 || sufferingSquad.pos4.hp != 0) reduced = true;
            } else {    //  FB vs FF
                if (sufferingSquad.pos3.hp != 0) {
                    reduced = true
                } else {
                    if ((atkP == 2 && sufferingSquad.pos1.hp != 0) || (atkP == 4 && sufferingSquad.pos5.hp != 0)) reduced = true
                }
            }
            reduced = true
        }
        if (reduced) {
            println("Ranged attack performed with disadvantage from pos$atkP")
            println("Ranged attack performed with disadvantage from pos$atkP")
            println("Ranged attack performed with disadvantage from pos$atkP")
            println("Ranged attack performed with disadvantage from pos$atkP")
            println("Ranged attack performed with disadvantage from pos$atkP")
            return accuracy / 2
        } else return accuracy
    }

    private fun randomizeDamage(damage: Int): Int {
        val minDmg: Int = damage * 100 - damage * 20
        val random = minDmg + Math.random() * damage * 40
        return (random / 100).roundToInt()
    }
}