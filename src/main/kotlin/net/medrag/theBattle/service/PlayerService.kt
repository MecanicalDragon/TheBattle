package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.PlayerDTO
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.repo.PlayerRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
@Service
class PlayerService(@Autowired private val playerRepo: PlayerRepo) {

    fun getPlayerByName(name: String): PlayerDTO {
        val player = playerRepo.findByName(name) ?: throw ValidationException("no such player")
        return PlayerDTO(player.name, player.games, player.wins)
    }

    fun createPlayer(name: String): PlayerDTO {
        if (name.length == name.trim().length && name.matches(regex.toRegex())) {
            val player = Player(null, name)
            try {
                playerRepo.save(player)
            } catch (e: Exception) {
                throw ValidationException("Player '$name' already exists.")
            }
            return PlayerDTO(name)
        } else throw ValidationException("String '$name' does not match regex '$regex'")
    }

    companion object {
        const val regex = "^[A-Za-z0-9]{4,16}\$";
    }
}