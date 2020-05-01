package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.model.entities.ProfileImage


/**
 * {@author} Stanislav Tretyakov
 * 28.01.2020
 */
data class PlayerDTO(
        val name: String,
        val games: Int = 0,
        val wins: Int = 0,
        val profileImage:ProfileImage = ProfileImage(),
        val status: PlayerStatus = PlayerStatus.FREE,
        var id: Long? = null)