package net.medrag.theBattle.service

import net.medrag.theBattle.model.ATTACK_VALIDATION_ERROR
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.Position.*
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.UnitEffects
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.roundToInt

@Service
class AttackService {

    /**
     * Validate attack for closed-range unit.
     * @param realActor Position
     * @param targets List<Position>
     * @param playerSquad ValidatedSquad
     * @param foesSquad ValidatedSquad
     * @throws ValidationException if target is not allowed or if all targets are already dead
     */
    @Throws(ValidationException::class)
    fun validateMeleeAttack(actor: Position, targets: List<Position>, playerSquad: ValidatedSquad, foesSquad: ValidatedSquad) {

        val player: ArrayList<Boolean> = arrayListOf(playerSquad.pos1.isAlive(), playerSquad.pos2.isAlive(),
                playerSquad.pos3.isAlive(), playerSquad.pos4.isAlive(), playerSquad.pos5.isAlive())
        val foe: ArrayList<Boolean> = arrayListOf(foesSquad.pos1.isAlive(), foesSquad.pos2.isAlive(),
                foesSquad.pos3.isAlive(), foesSquad.pos4.isAlive(), foesSquad.pos5.isAlive())
        val realAttacker = Position.values().indexOf(actor)

        var deadCounter = 0
        loop@ for (targetPosition in targets) {
            if (foesSquad.map[targetPosition]?.isDead() == true) {
                deadCounter++
                continue
            }
            var attacker = realAttacker
            var target = Position.values().indexOf(targetPosition)
            when (playerSquad.type) {
                SquadType.FORCED_FRONT -> when (foesSquad.type) {
                    SquadType.FORCED_FRONT -> {

                        //  If attacker is in the rear line, both of front units should be dead.
                        if (attacker % 2 == 1) {
                            if (player[attacker - 1] || player[attacker + 1])
                                throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                            else attacker = 2
                        } else {

                            //  if attacker and target are on different sides, one of central units should be dead.
                            if (abs(attacker - target) == 4) {
                                if (player[2].not() || foe[2].not()) continue@loop
                                else throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                            }
                        }

                        //  If target is adjacent unit, validate attack
                        if (attacker == target || abs(attacker - target) == 2) continue@loop

                        //  Target is in the back line. Both of front units should be dead
                        if (foe[target - 1] || foe[target + 1])
                            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                    }
                    SquadType.FORCED_BACK -> {

                        //  If attacker is in the rear line, both of front units should be dead.
                        if (attacker % 2 == 1) {
                            if (player[attacker - 1] || player[attacker + 1])
                                throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                            else attacker = 2
                        }

                        //  If target is in the first line
                        if (target % 2 == 1) {
                            if (player[2].not() || attacker == 2 || abs(attacker - target) == 1) continue@loop
                            else throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                        } else {

                            //  Target is in the back line

                            //  Validate if all units in first line are dead
                            if (foe[1].not() && foe[3].not()) continue@loop
                            if (foe[abs(target - 1)] || target == 2)
                                throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))

//                            //  Target is in POS1
//                            if (target == 0) {
//                                if (foe[1] || (foe[3] && attacker == 4 && player[2]))
//                                    throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
//                                else continue@loop
//
//                                //  Target is in POS5
//                            } else if (target == 4) {
//                                if (foe[3] || (foe[1] && attacker == 0 && player[2]))
//                                    throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
//                                else continue@loop
//
//                                //  Target is in POS3, but first line is not cleared
//                            } else throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                        }
                    }
                }
                SquadType.FORCED_BACK -> when (foesSquad.type) {
                    SquadType.FORCED_FRONT -> {

                        // If attacker is in the back line
                        if (attacker % 2 == 0) {
                            if (player[1].not() && player[3].not()) attacker = 1
                            else if (attacker == 2 || player[abs(attacker - 1)])
                                throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                            else attacker = 1
                        }

                        //  Target is in the rear line
                        if (target % 2 == 1) {
                            if (foe[target + 1].not() && foe[target - 1].not()) target = 2
                            else throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                        }

                        //  Target is in the first line
                        if (abs(target - attacker) == 1) continue@loop
                        else if (player[abs(target - 1)])
                            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))

                    }
                    SquadType.FORCED_BACK -> {

                        // If attacker is in the back line
                        if (attacker % 2 == 0) {
                            if (player[1].not() && player[3].not()) attacker = 1
                            else if (attacker == 2 || player[abs(attacker - 1)])
                                throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                            else attacker = 1
                        }

                        if (target % 2 == 1 || (foe[1].not() && foe[3].not())) continue@loop
                        if (foe[abs(target - 1)] || target == 2)
                            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                    }
                }
            }

        }
        if (deadCounter == targets.size)
            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
    }

    /**
     * Deal damage to target
     *
     * @param target UnitDTO
     * @param accuracy Int
     * @param attackPower Int
     * @return String - battle logs
     */
    fun sufferDamage(target: UnitDTO, accuracy: Int, attackPower: Int): String {
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
            if (target.isDead())
                comments.append(" and dies")
            comments.append(".")
            return comments.toString()
        } else {
            return "${target.name} dodges the attack!"
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