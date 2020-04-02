package net.medrag.theBattle.service

import net.medrag.theBattle.model.ATTACK_VALIDATION_ERROR
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.Position.*
import net.medrag.theBattle.model.squad.SquadType.*
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.springframework.stereotype.Service


/**
 * {@author} Stanislav Tretyakov
 * 01.04.2020
 */
//@Service
class MeleeAttackValidator {

    private val strongLine = listOf(POS1, POS3, POS5)
    private val weakLine = listOf(POS2, POS4)

    private enum class ConfrontationType(val validByDefault: Map<Position, List<Position>>) {
        FRONT_TO_FRONT(mapOf(Pair(POS1, listOf(POS1, POS3)), Pair(POS3, listOf(POS1, POS3, POS5)), Pair(POS5, listOf(POS5, POS3)))),
        FRONT_TO_BACK(mapOf(Pair(POS1, listOf(POS2)), Pair(POS3, listOf(POS2, POS4)), Pair(POS5, listOf(POS4)))),
        BACK_TO_BACK(mapOf(Pair(POS2, listOf(POS2, POS4)), Pair(POS4, listOf(POS2, POS4)))),
        BACK_TO_FRONT(mapOf(Pair(POS2, listOf(POS1, POS3)), Pair(POS4, listOf(POS5, POS3))));
    }

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

        //TODO: now we just mock validation
        if (Math.random() < 1) return

        val confrontationType: ConfrontationType = when (playerSquad.type) {
            FORCED_FRONT -> when (foesSquad.type) {
                FORCED_FRONT -> ConfrontationType.FRONT_TO_FRONT
                FORCED_BACK -> ConfrontationType.FRONT_TO_BACK
            }
            FORCED_BACK -> when (foesSquad.type) {
                FORCED_FRONT -> ConfrontationType.BACK_TO_FRONT
                FORCED_BACK -> ConfrontationType.BACK_TO_BACK
            }
        }

        for (target in targets) {
            if (confrontationType.validByDefault[actor]?.contains(target) == true) continue


            if (playerSquad.type === FORCED_FRONT) {

                // If attacker is in the rear line
                if (weakLine.contains(actor)) {
                    if (playerSquad.pos3.isAlive()) {
                        throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                    } else {
                        if ((actor === POS2 && playerSquad.pos1.isAlive()) || (actor === POS4 && playerSquad.pos5.isAlive())) {
                            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                        }
                    }
                }


                if (foesSquad.type === FORCED_FRONT) {
                    if (strongLine.contains(target)) {
                        if (playerSquad.pos3.isAlive() && foesSquad.pos3.isAlive())
                            throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                        else continue
                    }
                    if (foesSquad.pos3.isAlive()) throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                    if (target === POS4 && foesSquad.pos5.isAlive()) throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                    if (target === POS2 && foesSquad.pos1.isAlive()) throw ValidationException(String.format(ATTACK_VALIDATION_ERROR, actor, targets))
                }
            }
        }
    }

}