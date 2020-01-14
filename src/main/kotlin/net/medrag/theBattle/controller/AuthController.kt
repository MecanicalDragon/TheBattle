package net.medrag.theBattle.controller

import net.medrag.theBattle.model.LOGGED_OUT
import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.entities.Player
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

    @GetMapping("/login")
    fun getPlayer(@RequestParam name: String, request: HttpServletRequest): ResponseEntity<Player> = try {
        val player = playerService.getPlayerByName(name)
        val session = request.getSession(true)
        session.setAttribute(PLAYER_SESSION, player.name)
        //TODO: should be removed in release
        loginEmulation(player.name)
        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.status(204).build<Player>()
    }

    @PostMapping("/createPlayer")
    fun createPlayer(@RequestParam name: String, request: HttpServletRequest): ResponseEntity<String> = try {
        val player = playerService.createPlayer(name)
        val session = request.getSession(true)
        session.setAttribute(PLAYER_SESSION, player)
        //TODO: should be removed in release
        loginEmulation(player)
        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        request.getSession(false).invalidate()
        return ResponseEntity.ok(LOGGED_OUT)
    }
}