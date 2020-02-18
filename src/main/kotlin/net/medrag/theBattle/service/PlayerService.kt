package net.medrag.theBattle.service

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.PlayerDTO
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.repo.PlayerRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 *
 * Provides operations with player as user
 */
@Service
class PlayerService(
        @Autowired private val playerRepo: PlayerRepo,
        @Autowired private val pwEncoder: PasswordEncoder) {

    /**
     * Makes an attempt to login user
     * @param name String - player name
     * @param pw String - password
     * @return PlayerDTO - DTO of logged in user
     * @throws ValidationException if credentials are incorrect
     */
    @Throws(ValidationException::class)
    fun login(name: String, pw: String): PlayerDTO {
        val player = playerRepo.findByName(name) ?: throw ValidationException("There is no player with name $name")
        if (pwEncoder.matches(pw, player.password)) {
            return PlayerDTO(player.name, player.games, player.wins, player.status)
        } else throw ValidationException("Incorrect password for player $name")
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
    fun createPlayer(name: String, pw: String): PlayerDTO {
        if (name.matches(regex.toRegex())) {
            if (pw.matches(regex.toRegex())) {
                val encode = pwEncoder.encode(pw)
                val player = Player(null, name, password = encode)
                try {
                    playerRepo.save(player)
                } catch (e: Exception) {
                    throw IncompatibleDataException("Player with name '$name' already exists.")
                }
                return PlayerDTO(name)
            } else throw ValidationException("Your password does not match regex '$regex'")
        } else throw ValidationException("Your name does not match regex '$regex'")
    }

    /**
     * Just regex store
     */
    companion object {
        const val regex = "^[A-Za-z0-9]{4,16}\$"
    }
}