package net.medrag.theBattle.service

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.PlayerDTO
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.ProfileImage
import net.medrag.theBattle.repo.AvatarRepo
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.service.api.PlayerServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * @author Stanislav Tretyakov
 * 23.12.2019
 */
@Service
class PlayerService(
        @Autowired private val playerRepo: PlayerRepo,
        @Autowired private val avatarRepo: AvatarRepo,
        @Autowired private val pwEncoder: PasswordEncoder) : PlayerServiceApi {

    /**
     * Makes an attempt to login user
     * @param name String - player name
     * @param pw String - password
     * @return PlayerDTO - DTO of logged in user
     * @throws ValidationException if credentials are incorrect
     */
    @Throws(ValidationException::class)
    override fun login(name: String, pw: String): PlayerDTO {
        val player = playerRepo.findByName(name) ?: throw ValidationException("There is no player with name $name")
        if (pwEncoder.matches(pw, player.password)) {
            return PlayerDTO(player.name, player.games, player.wins, player.profileImage, player.status, player.id)
        } else throw ValidationException("Incorrect password for player $name")
    }

    /**
     * Retrieves player by it's name
     * @param name String
     * @return PlayerDTO
     * @throws ValidationException if there is no player with such name
     */
    @Throws(ValidationException::class)
    override fun getPlayerData(name: String): PlayerDTO {
        playerRepo.findByName(name)?.let {
            return PlayerDTO(it.name, it.games, it.wins, it.profileImage, it.status)
        }
        throw ValidationException("No such player in database")
    }

    /**
     * Tries to create new user
     * @param name String - player name
     * @param pw String - password
     * @return PlayerDTO - DTO of newly created and logged in user
     * @throws ValidationException if input data does not match regex
     * @throws IncompatibleDataException if player with this name already exists in database
     */
    @Throws(ValidationException::class, IncompatibleDataException::class)
    override fun createPlayer(name: String, pw: String): PlayerDTO {
        if (name.matches(REGEX.toRegex())) {
            if (pw.matches(REGEX.toRegex())) {
                val encode = pwEncoder.encode(pw)
                val player = Player(null, name, password = encode)
                player.profileImage.name = "dragon1"
                try {
                    playerRepo.save(player)
                } catch (e: Exception) {
                    throw IncompatibleDataException("Player with name '$name' already exists.")
                }
                return PlayerDTO(name, id = player.id, profileImage = player.profileImage)
            } else throw ValidationException("Your password does not match regex '$REGEX'")
        } else throw ValidationException("Your name does not match regex '$REGEX'")
    }

    /**
     * Saves new profile image for the player
     * @param id Long - player id
     * @param ava ProfileImage
     * @throws ValidationException if player id is incorrect or colors don't match hex pattern
     */
    @Transactional
    @Throws(ValidationException::class)
    override fun saveNewProfileImage(id: Long, ava: ProfileImage) {
        if (!ava.background.matches(HEX.toRegex())
                || !ava.color.matches(HEX.toRegex())
                || !ava.borders.matches(HEX.toRegex())) {
            throw ValidationException("Color hex is incorrect!")
        }
        if (avatarRepo.findByImage(ava.name) == null) throw ValidationException("Image name is invalid!")
        val player: Player = playerRepo.findById(id).orElseThrow { ValidationException("No such player in the database.") }
        player.profileImage = ava
        playerRepo.save(player)
    }

    /**
     * Returns links to available avatars. It's here because in future available avatars will be calculated depending
     * on player's achievements.
     * @param page Int
     * @return Page<String>
     */
    override fun getAvatarsPage(page: Int) = avatarRepo.findAll(PageRequest.of(page, 10)).map { it.image }

    /**
     * Just regex store
     */
    companion object {
        const val REGEX = "^[A-Za-z0-9]{4,16}\$"
        const val HEX = "^#[0-9a-f]{6}\$"
    }
}