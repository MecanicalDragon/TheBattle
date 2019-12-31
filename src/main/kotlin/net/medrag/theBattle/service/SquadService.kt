package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.buildUnit
import net.medrag.theBattle.model.dto.buildUnitEntity
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@Service
class SquadService(@Autowired val unitRepo: UnitRepo, @Autowired val playerRepo: PlayerRepo) {

    /**
     * Returns Player's hero pool or empty list
     */
    fun getPool(playerName: String): List<UnitDTO> {
        val list = unitRepo.findAllByPlayer_Name(playerName)
        val result = ArrayList<UnitDTO>(list.size)
        for (unit in list) result.add(buildUnit(unit))
        return result;
    }

    fun addNewUnit(pName: String, name: String, type: Unitt.Unit.Type): UnitDTO {
        if (name.matches(Regex(regex))) {
            val id = playerRepo.getIdByName(pName) ?: throw ValidationException("Player with this name doesn't exist.")
            val player = Player(id, pName)
            val unit = buildUnitEntity(name, type, player)
            val saved = unitRepo.save(unit)
            return buildUnit(saved)
        } else throw ValidationException("Hero name doesn't match the pattern $regex")
    }

    @Transactional
    fun deleteUnit(id: Long, playerName: String) {
        val playerId = playerRepo.getIdByName(playerName) ?: throw ValidationException("Player with this name doesn't exist.")
        val player = Player(playerId, playerName)
        unitRepo.deleteUnit(id, player)
    }

    companion object {
        const val regex = "^[A-Za-z0-9]{2,16}\$";
    }
}