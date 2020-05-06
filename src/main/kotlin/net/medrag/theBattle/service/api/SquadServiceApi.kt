package net.medrag.theBattle.service.api

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.ManagePageResponse
import net.medrag.theBattle.model.dto.UnitDTO


/**
 * @author Stanislav Tretyakov
 * 06.05.2020
 */
interface SquadServiceApi {

    /**
     * Returns Player's hero pool or empty list
     * @param playerName String
     * @return List<UnitDTO> player's heroes
     */
    fun getPool(playerName: String): List<UnitDTO>

    /**
     * Retrieves player data for manage page start request
     * @param playerName String
     * @param pool List<UnitDTO> - list of free heroes
     * @return ManagePageResponse - data for render manage page
     * @throws IncompatibleDataException if playerName is incorrect
     */
    @Throws(IncompatibleDataException::class)
    fun compileResponse(playerName: String, pool: List<UnitDTO>): ManagePageResponse

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
    fun addNewUnit(pName: String, name: String, type: Unitt.Unit.Type): UnitDTO

    /**
     * Removes unit
     * @param id Long - unit id
     * @param playerName String
     * @throws IncompatibleDataException if somehow player absent in database
     */
    @Throws(IncompatibleDataException::class)
    fun deleteUnit(id: Long, playerName: String)
}