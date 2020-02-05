package net.medrag.theBattle.service

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
 */
@Service
class PlayerService(
        @Autowired private val playerRepo: PlayerRepo,
        @Autowired private val pwEncoder: PasswordEncoder) {

    fun getPlayerByName(name: String, pw: String): PlayerDTO {
        val player = playerRepo.findByName(name) ?: throw ValidationException("There is no player with name $name")
        if (pwEncoder.matches(pw, player.password)) {
            return PlayerDTO(player.name, player.games, player.wins)
        } else throw ValidationException("Incorrect password for player $name")
    }

    fun createPlayer(name: String, pw: String): PlayerDTO {
        if (name.matches(regex.toRegex())) {
            if (pw.matches(regex.toRegex())) {
                val encode = pwEncoder.encode(pw)
                val player = Player(null, name, password = encode)
                try {
                    playerRepo.save(player)
                } catch (e: Exception) {
                    throw ValidationException("Player with name '$name' already exists.")
                }
                return PlayerDTO(name)
            } else throw ValidationException("Your password does not match regex '$regex'")
        } else throw ValidationException("Your name does not match regex '$regex'")
    }

    companion object {
        const val regex = "^[A-Za-z0-9]{4,16}\$"
    }
}