package net.medrag.theBattle.model.classes

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.squad.SquadType


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class ValidatedSquad(val playerName: String,
                          val type: SquadType = SquadType.FORCED_FRONT,
                          val pos1: UnitDTO = mock,
                          val pos2: UnitDTO = mock,
                          val pos3: UnitDTO = mock,
                          val pos4: UnitDTO = mock,
                          val pos5: UnitDTO = mock) {

    @JsonIgnore
    val map = mapOf(Position.POS1 to pos1, Position.POS2 to pos2, Position.POS3 to pos3, Position.POS4 to pos4, Position.POS5 to pos5)

    companion object {
        val mock = UnitDTO(0, "Mock", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())

        //TODO: remove?
        fun sufferDamage(squad: ValidatedSquad, damaged: Map<String, UnitDTO>): ValidatedSquad {
            val pos1 = damaged["pos1"] ?: squad.pos1
            val pos2 = damaged["pos2"] ?: squad.pos2
            val pos3 = damaged["pos3"] ?: squad.pos3
            val pos4 = damaged["pos4"] ?: squad.pos4
            val pos5 = damaged["pos5"] ?: squad.pos5
            return ValidatedSquad(squad.playerName, squad.type, pos1, pos2, pos3, pos4, pos5)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidatedSquad) return false

        if (playerName != other.playerName) return false
        return true
    }

    override fun hashCode() = playerName.hashCode()
}