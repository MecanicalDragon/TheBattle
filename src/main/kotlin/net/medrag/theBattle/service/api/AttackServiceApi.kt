package net.medrag.theBattle.service.api

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.ValidatedSquad


/**
 * @author Stanislav Tretyakov
 * 06.05.2020
 */
interface AttackServiceApi {

    /**
     * Validate attack for closed-range unit.
     * @param realActor Position
     * @param targets List<Position>
     * @param playerSquad ValidatedSquad
     * @param foesSquad ValidatedSquad
     * @throws ValidationException if attack is invalid
     */
    @Throws(ValidationException::class)
    fun validateMeleeAttack(actor: Position, targets: List<Position>, playerSquad: ValidatedSquad, foesSquad: ValidatedSquad)

    /**
     * Deal damage to target
     *
     * @param target UnitDTO
     * @param accuracy Int
     * @param attackPower Int
     * @return String - battle logs
     */
    fun sufferDamage(target: UnitDTO, accuracy: Int, attackPower: Int): String

    /**
     * Checks if ranged unit attacks from a front line and reduces it's accuracy in this case
     *
     * @param accuracy Int unit's accuracy
     * @param pos Position - attacking unit position
     * @param squadType SquadType - attacker's squad type
     * @return Int - current accuracy
     */
    fun calculateAccuracy(accuracy: Int, pos: Position, squadType: SquadType): Int
}