package net.medrag.theBattle.service

import net.medrag.theBattle.model.ATTACK_VALIDATION_ERROR
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.UnitEffects
import net.medrag.theBattle.model.squad.SquadType
import org.springframework.stereotype.Service
import kotlin.math.roundToInt
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.slf4j.LoggerFactory
import kotlin.math.abs

@Service
class AttackService {

    /**
     * Validate attack for closed-range unit.
     * @param realActor Position
     * @param targets List<Position>
     * @param playerSquad ValidatedSquad
     * @param foesSquad ValidatedSquad
     * @throws ValidationException if attack is invalid
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
                                logAndThrow("e001", actor, targetPosition, playerSquad, foesSquad)
                            else attacker = 2
                        } else {

                            //  if attacker and target are on different sides, one of central units should be dead.
                            if (abs(attacker - target) == 4) {
                                if (player[2].not() || foe[2].not()) continue@loop
                                else logAndThrow("e002", actor, targetPosition, playerSquad, foesSquad)
                            }
                        }

                        //  If target is adjacent unit, validate attack
                        if (attacker == target || abs(attacker - target) == 2) continue@loop

                        //  Target is in the back line. Both of front units should be dead
                        if (foe[target - 1] || foe[target + 1])
                            logAndThrow("e003", actor, targetPosition, playerSquad, foesSquad)
                    }
                    SquadType.FORCED_BACK -> {

                        //  If attacker is in the rear line, both of front units should be dead.
                        if (attacker % 2 == 1) {
                            if (player[attacker - 1] || player[attacker + 1])   //  condition 1
                                logAndThrow("e004", actor, targetPosition, playerSquad, foesSquad)
                            else attacker = 2
                        }

                        //  If target is in the first line
                        if (target % 2 == 1) {
                            if (player[2].not() || attacker == 2 || abs(attacker - target) == 1
                                    || foe[abs(attacker - 1)].not()) continue@loop  //  condition 2
                            else logAndThrow("e005", actor, targetPosition, playerSquad, foesSquad)
                        } else {

                            //  Target is in the back line
                            if (foe[1].not() && foe[3].not()) continue@loop //  condition 3
                            if (foe[abs(target - 1)] || target == 2)    //  condition 4
                                logAndThrow("e006", actor, targetPosition, playerSquad, foesSquad)

                        }
                    }
                }
                SquadType.FORCED_BACK -> when (foesSquad.type) {
                    SquadType.FORCED_FRONT -> {

                        // If attacker is in the back line
                        if (attacker % 2 == 0) {
                            if (player[1].not() && player[3].not()) attacker = 1
                            else if (attacker == 2 || player[abs(attacker - 1)])
                                logAndThrow("e007", actor, targetPosition, playerSquad, foesSquad)
                            else attacker = 1
                        }

                        //  Target is in the rear line
                        if (target % 2 == 1) {
                            if (foe[target + 1].not() && foe[target - 1].not()) target = 2
                            else logAndThrow("e008", actor, targetPosition, playerSquad, foesSquad)
                        }

                        //  Target is in the first line
                        if (abs(target - attacker) == 1) continue@loop
                        else if (player[abs(target - 1)])
                            logAndThrow("e009", actor, targetPosition, playerSquad, foesSquad)

                    }
                    SquadType.FORCED_BACK -> {

                        // If attacker is in the back line
                        //TODO: check intently!
                        if (attacker % 2 == 0) {
                            if (player[1] || player[3]) {
                                if (attacker == 2 || player[abs(attacker - 1)])
                                    logAndThrow("e010", actor, targetPosition, playerSquad, foesSquad)
                            }
                        }

                        if (target % 2 == 1 || (foe[1].not() && foe[3].not())) continue@loop
                        if (foe[abs(target - 1)] || target == 2)
                            logAndThrow("e011", actor, targetPosition, playerSquad, foesSquad)
                    }
                }
            }

        }
        if (deadCounter == targets.size) {
            log.error("All assigned targets for ${playerSquad.map[actor]} are already dead!")
            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, "e000", actor, targets))
        }
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
        val modifier = Math.random()
        if (modifier * accuracy > target.type.evasion) {
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
            log.debug("Attack on ${target.name} fails with accuracy = $accuracy and $modifier as modifier value " +
                    "against basic evasion of ${target.type.evasion}.")
            return "${target.name} dodges the attack!"
        }
    }

    /**
     * Checks if ranged unit attacks from a front line and reduces it's accuracy in this case
     *
     * @param accuracy Int unit's accuracy
     * @param pos Position - attacking unit position
     * @param squadType SquadType - attacker's squad type
     * @return Int - current accuracy
     */
    fun calculateAccuracy(accuracy: Int, pos: Position, squadType: SquadType): Int =
            if ((squadType === SquadType.FORCED_FRONT && pos.isStrongLine())
                    || (squadType === SquadType.FORCED_BACK && pos.isWeakLine())) {
                log.debug("Accuracy value $accuracy for unit on position $pos will be reduced by 2 because of " +
                        "RANGED type attack from the FRONT line.")
                accuracy / 2
            } else accuracy

    private fun randomizeDamage(damage: Int): Int {
        val minDmg: Int = damage * 100 - damage * 20
        val random = minDmg + Math.random() * damage * 40
        return (random / 100).roundToInt()
    }

    @Throws(ValidationException::class)
    private fun logAndThrow(errorCode: String, attacker: Position, target: Position, player: ValidatedSquad, foe: ValidatedSquad) {
        val err = String.format(ATTACK_VALIDATION_ERROR, errorCode, attacker, target)
        log.error(err)
        log.error("Player's squad: $player")
        log.error("Player's positions: ${player.map}")
        log.error("Enemy's squad: $foe")
        log.error("Enemy's positions: ${foe.map}")
        throw ValidationException(err)
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val log = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}