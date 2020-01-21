package net.medrag.theBattle.controller

import net.medrag.theBattle.model.BATTLE_UUID
import net.medrag.theBattle.model.PLAYER_SESSION
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
fun extractPlayerName(request: HttpServletRequest, pName: String?): String? {
    var playerName = request.getSession(false)?.getAttribute(PLAYER_SESSION) as? String
    if (playerName == null) {
        if (pName != null && pName.isNotBlank()) {
//            println("========================================= no session request ================================================")
            playerName = sessionStorage[pName]?.playerName
        } else {
            println("==============> No session and no valid 'pName' parameter in the request. This request will not be handled.")
            return null
        }
    } else {
        println("++++++++++++++++ $playerName request ++++++++++++++++")
    }
    return playerName
}

fun extractBattleUUID(request: HttpServletRequest, pName: String?): UUID? {
    request.getSession(false)?.let {
        return it.getAttribute(BATTLE_UUID) as? UUID

    }
//    println("========================================= no session UUID request ================================================")
    return sessionStorage[pName]?.bud
}

fun emulateBudSetting(pName: String, bud: UUID, request: HttpServletRequest) {
    request.getSession(false)?.let {
        it.setAttribute(BATTLE_UUID, bud)
        return
    }
    sessionStorage[pName]?.bud = bud
}

fun loginEmulation(pName: String) {
    sessionStorage[pName] = UserData(pName)
}

val sessionStorage: ConcurrentHashMap<String, UserData> = ConcurrentHashMap()

class UserData(
        var playerName: String? = null,
        var bud: UUID? = null
)