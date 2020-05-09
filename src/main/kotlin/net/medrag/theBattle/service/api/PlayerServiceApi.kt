package net.medrag.theBattle.service.api

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.PlayerDTO
import net.medrag.theBattle.model.entities.ProfileImage
import org.springframework.data.domain.Page


/**
 * Player-related operations.
 * @author Stanislav Tretyakov
 * 06.05.2020
 */
interface PlayerServiceApi {

    /**
     * Makes an attempt to login user
     * @param name String - player name
     * @param pw String - password
     * @return PlayerDTO - DTO of logged in user
     * @throws ValidationException if credentials are incorrect
     */
    @Throws(ValidationException::class)
    fun login(name: String, pw: String): PlayerDTO

    /**
     * Retrieves player by it's name
     * @param name String
     * @return PlayerDTO
     * @throws ValidationException if there is no player with such name
     */
    @Throws(ValidationException::class)
    fun getPlayerData(name: String): PlayerDTO

    /**
     * Tries to create new user
     * @param name String - player name
     * @param pw String - password
     * @return PlayerDTO - DTO of newly created and logged in user
     * @throws ValidationException if input data does not match regex
     * @throws IncompatibleDataException if player with this name already exists in database
     */
    @Throws(ValidationException::class, IncompatibleDataException::class)
    fun createPlayer(name: String, pw: String): PlayerDTO

    /**
     * Saves new profile image for the player
     * @param id Long - player id
     * @param ava ProfileImage
     * @throws ValidationException if player id is incorrect or colors don't match hex pattern
     */
    @Throws(ValidationException::class)
    fun saveNewProfileImage(id: Long, ava: ProfileImage)

    /**
     * Returns links to available avatars. It's here because in future available avatars will be calculated depending
     * on player's achievements.
     * @param page Int
     * @return Page<String>
     */
    fun getAvatarsPage(page: Int): Page<String>

}