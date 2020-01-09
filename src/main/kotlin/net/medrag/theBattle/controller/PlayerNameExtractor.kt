package net.medrag.theBattle.controller

import net.medrag.theBattle.model.PLAYER_SESSION
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
fun extractPlayerName(request: HttpServletRequest, pName: String?): String? {
    var playerName = request.getSession(false)?.getAttribute(PLAYER_SESSION) as? String
    if (playerName == null) {
        if (pName != null && pName.isNotBlank()) {
            println("========================================= no session request ================================================")
            playerName = pName
        } else {
            println("==============> No session and no valid 'pName' parameter in the request. This request will not be handled.")
            return null
        }
    } else {
            println("++++++++++++++++ $playerName request ++++++++++++++++")
    }
    return playerName;
}