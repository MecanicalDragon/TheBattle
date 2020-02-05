package net.medrag.theBattle.controller

import net.medrag.theBattle.config.PPPair
import net.medrag.theBattle.model.LOGGED_OUT
import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.PlayerDTO
import net.medrag.theBattle.service.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val playerService: PlayerService) {

    @PostMapping("/login")
    fun login(@RequestBody pair: PPPair, request: HttpServletRequest): ResponseEntity<Any> = try {

        val player = playerService.getPlayerByName(pair.name, pair.pw)
        val session = request.getSession(true)
        session.setAttribute(PLAYER_SESSION, player.name)

        //TODO: should be removed in release
        loginEmulation(player.name)

        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @PostMapping("/createPlayer")
    fun createPlayer(@RequestBody pair: PPPair, request: HttpServletRequest): ResponseEntity<Any> = try {

        val player = playerService.createPlayer(pair.name, pair.pw)
        val session = request.getSession(true)
        session.setAttribute(PLAYER_SESSION, player)

        //TODO: should be removed in release
        loginEmulation(player.name)

        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    //TODO: what about logging out during game search?
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        request.getSession(false)?.invalidate()
        return ResponseEntity.ok(LOGGED_OUT)
    }
}