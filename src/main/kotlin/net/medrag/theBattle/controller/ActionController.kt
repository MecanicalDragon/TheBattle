package net.medrag.theBattle.controller

import net.medrag.theBattle.model.BATTLE_UUID
import net.medrag.theBattle.model.PLAYER_NAME
import net.medrag.theBattle.model.PLAYER_STATUS
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.SimpleAction
import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.service.ActionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 15.01.2020
 *
 * Controller for handling battle actions
 */
@RestController
@RequestMapping("/action")
class ActionController(private val actionService: ActionService) {

    /**
     * SimpleAction handling
     * @param action SimpleAction
     * @param request HttpServletRequest
     * @return ResponseEntity<Any>:
     *      - 200 if action successfully handled
     *      - 400 if action is invalid
     *      - 401 if httpSession is missed
     *      - 555 if db fails
     */
    @PostMapping("/performAction")
    fun performAction(@RequestBody action: SimpleAction, request: HttpServletRequest): ResponseEntity<Any> {

        val session = request.getSession(false)
        if (session != null) {
            val playerName = session.getAttribute(PLAYER_NAME) as? String
            if (playerName != null) {
                (session.getAttribute(BATTLE_UUID) as? UUID)?.let {
                    return try {
                        val actionResult = actionService.performSimpleAction(playerName, it, action)
                        if (actionResult.finished)
                            session.removeAttribute(BATTLE_UUID)
                        ResponseEntity.ok(actionResult)
                    } catch (e: ValidationException) {
                        ResponseEntity.badRequest().body(e.message)
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}