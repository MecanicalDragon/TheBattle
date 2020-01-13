package net.medrag.theBattle.model.classes

import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.squad.SquadType
import java.util.*


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
                          val pos5: UnitDTO = mock,
                          val bud: UUID = UUID.randomUUID()) {  //  BattleUUID

    companion object {
        val mock = UnitDTO(0, "Mock", 0, 0, 0, Unitt.Unit.Type.FIGHTER.getInstance())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidatedSquad) return false

        if (playerName != other.playerName) return false
        if (bud != other.bud) return false

        return true
    }

    override fun hashCode(): Int {
        var result = playerName.hashCode()
        result = 31 * result + bud.hashCode()
        return result
    }
}