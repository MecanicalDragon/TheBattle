package net.medrag.theBattle.model.classes

import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.squad.SquadType


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class Squad(val player: Player,
                 val type: SquadType,
                 val pos1: UnitDTO,
                 val pos2: UnitDTO,
                 val pos3: UnitDTO,
                 val pos4: UnitDTO,
                 val pos5: UnitDTO) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Squad) return false

        if (player != other.player) return false

        return true
    }

    override fun hashCode(): Int {
        return player.hashCode()
    }

    companion object {
        val mock = UnitDTO(0, "Mock", 0, 0, 0, Unitt.Unit.Type.FIGHTER.getInstance())
    }
}