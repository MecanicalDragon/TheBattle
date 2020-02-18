package net.medrag.theBattle.controller

import net.medrag.theBattle.config.PPPair
import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.LOGGED_OUT
import net.medrag.theBattle.model.PLAYER_NAME
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.service.PlayerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession


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
     * Checks if user authenticated
     * @param httpSession HttpSession
     * @return ResponseEntity - 'authenticated' boolean
     */
    @GetMapping("/isAuthenticated")
    fun isAuthenticated(httpSession: HttpSession) = ResponseEntity.ok(httpSession.getAttribute(PLAYER_NAME) != null)

    /**
     * Checks if user authenticated
     * @param httpSession HttpSession
     * @return ResponseEntity:
     *      - 200 if playerDTO is nested
     *      - 401 if player unauthorized
     *      - 555 if database fails
     */
    @GetMapping("/isAuthenticatedWithData")
    fun isAuthenticatedWithData(httpSession: HttpSession): ResponseEntity<Any> {
        (httpSession.getAttribute(PLAYER_NAME) as? String)?.let {
            try {
                return ResponseEntity.ok(playerService.getPlayerData(it))
            } catch (e: ValidationException) {
                httpSession.invalidate()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

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
        (session.getAttribute(PLAYER_NAME) as? String)?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.login(pair.name, pair.pw)
        session.setAttribute(PLAYER_NAME, player.name)
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
        (session.getAttribute(PLAYER_NAME) as? String)?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.createPlayer(pair.name, pair.pw)
        session.setAttribute(PLAYER_NAME, player.name)
        ResponseEntity.ok(player)
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IncompatibleDataException) {
        ResponseEntity(HttpStatus.CONFLICT)
    }

    //TODO: cancel the battle search if logout
    //TODO: concede the battle if logout
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