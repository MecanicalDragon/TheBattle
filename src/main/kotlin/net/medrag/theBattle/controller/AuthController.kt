package net.medrag.theBattle.controller

import net.medrag.theBattle.config.PPPair
import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.ProcessingException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.ActionType
import net.medrag.theBattle.model.dto.IndexPageResponse
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.SimpleAction
import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.model.PlayerSession
import net.medrag.theBattle.service.api.ActionServiceApi
import net.medrag.theBattle.service.api.AdServiceApi
import net.medrag.theBattle.service.api.BattleServiceApi
import net.medrag.theBattle.service.api.PlayerServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession


/**
 * @author Stanislav Tretyakov
 * 23.12.2019
 *
 * Provides authentication operations
 */
@RestController
@RequestMapping("/auth")
class AuthController(@Autowired private val playerService: PlayerServiceApi,
                     @Autowired private val session: PlayerSession,
                     @Autowired private val battleService: BattleServiceApi,
                     @Autowired private val actionService: ActionServiceApi,
                     @Autowired private val adService: AdServiceApi) {

    /**
     * Checks if user authenticated
     * @return ResponseEntity:
     *      - 200 if playerDTO is nested
     *      - 401 if player unauthorized
     *      - 555 if database fails
     */
    @GetMapping("/isAuthenticatedWithData")
    fun isAuthenticatedWithData(): ResponseEntity<Any> {
        session.playerName?.let {
            try {
                val playerDto = playerService.getPlayerData(it)
                session.playerStatus = playerDto.status
                return ResponseEntity.ok(IndexPageResponse(playerDto, newsUrl = adService.getRandomLink()))
            } catch (e: ValidationException) {
                session.invalidate()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Login attempt.
     * @param pair PPPair - name\password pair
     * @return ResponseEntity<Any>:
     *      - 200 with PlayerDTO if login was successful
     *      - 400 with error message if bad cred have been input
     *      - 428 if user already logged in
     */
    @PostMapping("/login")
    fun login(@RequestBody pair: PPPair): ResponseEntity<Any> = try {

        session.playerName?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.login(pair.name, pair.pw)
        session.playerId = player.id.also { player.id = session.playerId }
        session.playerName = player.name
        session.playerStatus = player.status
        ResponseEntity.ok(IndexPageResponse(player, newsUrl = adService.getRandomLink()))
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    }

    /**
     * Create new player and login with it.
     * @param pair PPPair - name\password pair
     * @return ResponseEntity<Any>:
     *      - 200 with PlayerDTO if login was successful
     *      - 400 with error message if bad cred have been input
     *      - 428 if user already logged in
     *      - 409 if user with this name already exists
     */
    @PostMapping("/createPlayer")
    fun createPlayer(@RequestBody pair: PPPair): ResponseEntity<Any> = try {

        session.playerName?.let { return ResponseEntity(HttpStatus.PRECONDITION_REQUIRED) }
        val player = playerService.createPlayer(pair.name, pair.pw)
        session.playerId = player.id.also { player.id = session.playerId }
        session.playerName = player.name
        session.playerStatus = player.status
        ResponseEntity.ok(IndexPageResponse(player, newsUrl = adService.getRandomLink()))
    } catch (e: ValidationException) {
        ResponseEntity.badRequest().body(e.message)
    } catch (e: IncompatibleDataException) {
        ResponseEntity(HttpStatus.CONFLICT)
    }

    /**
     * Logout
     * @return ResponseEntity<Void>:
     *      - 200 if logout was successful
     *      - 230 if player obtains a second chance to think once more, cause the battle just started.
     */
    //TODO: what about clearing cookies during battle or game searching by both players?
    @PostMapping("/logout")
    fun logout(httpSession: HttpSession): ResponseEntity<Void> {
        try {
            session.playerName?.let { name ->
                if (session.playerStatus === PlayerStatus.IN_SEARCH) {
                    session.playerId?.let {
                        battleService.cancelBid(it, name)
                    }
                } else if (session.playerStatus === PlayerStatus.IN_BATTLE) {
                    session.bud?.let {
                        actionService.performSimpleAction(name, it, SimpleAction(Position.POS1, ActionType.CONCEDE, emptyMap()))
                    }
                }
                httpSession.invalidate()
            }
        } catch (e: ProcessingException) {
            return ResponseEntity.status(230).build()
        }
        return ResponseEntity.status(200).build()
    }
}