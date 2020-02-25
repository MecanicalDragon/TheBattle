package net.medrag.theBattle.service

import net.medrag.theBattle.model.entities.PlayerStatus
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope
import java.util.*


/**
 * Contains all data, related to a player
 * {@author} Stanislav Tretyakov
 * 25.02.2020
 */
@Component
@SessionScope
class PlayerSession {
    var playerId: Long? = null
    var playerName: String? = null
    var playerStatus: PlayerStatus = PlayerStatus.FREE
    var gamesTotal: Int = 0
    var winsCount: Int = 0
    var bud: UUID? = null

    fun invalidate() {
        playerId = null
        playerName = null
        playerStatus = PlayerStatus.FREE
        gamesTotal = 0
        winsCount = 0
        bud = null
    }
}