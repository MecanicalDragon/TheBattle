package net.medrag.theBattle.service

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.UnitStatus
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import net.medrag.theBattle.service.api.UnitServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * @author Stanislav Tretyakov
 * 19.12.2019
 */
@Service
class UnitService(@Autowired val unitRepo: UnitRepo, @Autowired val playerRepo: PlayerRepo) : UnitServiceApi {

    /**
     * Returns Player's hero pool or empty list
     * @param playerName String
     * @return List<UnitDTO> player's heroes
     */
    override fun getPool(playerName: String): List<UnitDTO> =
            unitRepo.findAllByPlayer_NameAndStatus(playerName, UnitStatus.IN_POOL).map { buildUnit(it) }

    /**
     * Adding new hero in pool
     * @param pName String - player name
     * @param name String - hero name
     * @param type Type - unit type
     * @return UnitDTO - newly created unit
     * @throws ValidationException if unit name is invalid
     * @throws IncompatibleDataException - if player with specified name does not exist
     */
    @Throws(ValidationException::class, IncompatibleDataException::class)
    override fun addNewUnit(pName: String, name: String, type: Unitt.Unit.Type): UnitDTO {
        if (name.trim().matches(Regex(regex))) {
            val id = playerRepo.getIdByName(pName)
                    ?: throw IncompatibleDataException("Player with specified name '$pName' name doesn't exist. Seriously?")
            val player = Player(id, pName)
            val unit = buildUnitEntity(name, type, player)
            val saved = unitRepo.save(unit)
            return buildUnit(saved)
        } else throw ValidationException("Hero name doesn't match the pattern $regex")
    }

    /**
     * Removes unit
     * @param id Long - unit id
     * @param playerName String
     * @throws IncompatibleDataException if somehow player absent in database
     */
    @Transactional
    @Throws(IncompatibleDataException::class)
    override fun deleteUnit(id: Long, playerName: String) {
        val playerId = playerRepo.getIdByName(playerName)
                ?: throw IncompatibleDataException("Player with this name doesn't exist.")
        val player = Player(playerId, playerName)
        unitRepo.deleteUnit(id, player)
    }

    companion object {
        const val regex = "^[A-Za-z 0-9]{2,16}\$";
    }
}