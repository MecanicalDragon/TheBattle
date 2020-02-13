package net.medrag.theBattle.controller

import net.medrag.theBattle.config.PPPair
import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.LOGGED_OUT
import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.service.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 *
 * Provides authentication operations
 */
@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val playerService: PlayerService) {

    /**
     * Login attempt.
     * @param pair PPPair - name\password pair
     * @param request HttpServletRequest
     * @return ResponseEntity<Any>:
     *      - 200 with PlayerDTO if login was successful
     *      - 400 with error message if bad cred have been input
     *      - 428 if user already logged in
     */
    @PostMapping("/login")
    fun login(@RequestBody pair: PPPair, request: HttpServletRequest): ResponseEntity<Any> = try {

        val session = request.getSession(true)
        (session.getAttribute(PLAYER_SESSION) as? String)?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.login(pair.name, pair.pw)
        session.setAttribute(PLAYER_SESSION, player.name)
        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    /**
     * Create new player and login with it.
     * @param pair PPPair - name\password pair
     * @param request HttpServletRequest
     * @return ResponseEntity<Any>:
     *      - 200 with PlayerDTO if login was successful
     *      - 400 with error message if bad cred have been input
     *      - 428 if user already logged in
     *      - 409 if user with this name already exists
     */
    @PostMapping("/createPlayer")
    fun createPlayer(@RequestBody pair: PPPair, request: HttpServletRequest): ResponseEntity<Any> = try {

        val session = request.getSession(true)
        (session.getAttribute(PLAYER_SESSION) as? String)?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.createPlayer(pair.name, pair.pw)
        session.setAttribute(PLAYER_SESSION, player.name)
        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IncompatibleDataException) {
        ResponseEntity(HttpStatus.CONFLICT)
    }

    //TODO: what about logging out during game search?
    //TODO: what about clearing cookies during battle or game searching by one or two players?
    /**
     * Logout
     * @param request HttpServletRequest
     * @return ResponseEntity<String> - LOGGED_OUT const string
     */
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<String> {
        request.getSession(false)?.invalidate()
        return ResponseEntity.ok(LOGGED_OUT)
    }
}