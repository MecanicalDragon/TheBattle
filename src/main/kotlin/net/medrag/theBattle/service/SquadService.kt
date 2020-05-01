package net.medrag.theBattle.service

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.*
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.UnitStatus
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 * Squad manage actions processing
 */
@Service
class SquadService(@Autowired val unitRepo: UnitRepo, @Autowired val playerRepo: PlayerRepo) {

    /**
     * Returns Player's hero pool or empty list
     * @param playerName String
     * @return List<UnitDTO> player's heroes
     */
    fun getPool(playerName: String): List<UnitDTO> {
        val list = unitRepo.findAllByPlayer_NameAndStatus(playerName, UnitStatus.IN_POOL)
        val result = ArrayList<UnitDTO>(list.size)
        for (unit in list) result.add(buildUnit(unit))
        return result;
    }


    /**
     * Retrieves player data for manage page start request
     * @param playerName String
     * @param pool List<UnitDTO> - list of free heroes
     * @return ManagePageResponse - data for render manage page
     * @throws IncompatibleDataException if playerName is incorrect
     */
    @Throws(IncompatibleDataException::class)
    fun compileResponse(playerName: String, pool: List<UnitDTO>): ManagePageResponse {
        val player = playerRepo.findByName(playerName)
                ?: throw IncompatibleDataException("Invalid playername: $playerName")
        return ManagePageResponse(PlayerDTO(playerName, player.games, player.wins, player.profileImage, player.status), pool)
    }

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
    fun addNewUnit(pName: String, name: String, type: Unitt.Unit.Type): UnitDTO {
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
    @Throws(IncompatibleDataException::class)
    @Transactional
    fun deleteUnit(id: Long, playerName: String) {
        val playerId = playerRepo.getIdByName(playerName)
                ?: throw IncompatibleDataException("Player with this name doesn't exist.")
        val player = Player(playerId, playerName)
        unitRepo.deleteUnit(id, player)
    }

    companion object {
        const val regex = "^[A-Za-z 0-9]{2,16}\$";
    }
}