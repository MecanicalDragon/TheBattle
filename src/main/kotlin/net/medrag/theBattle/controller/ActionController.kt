package net.medrag.theBattle.controller

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.SimpleAction
import net.medrag.theBattle.service.ActionService
import net.medrag.theBattle.service.PlayerSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


/**
 * {@author} Stanislav Tretyakov
 * 15.01.2020
 *
 * Controller for handling battle actions
 */
@RestController
@RequestMapping("/action")
class ActionController(@Autowired private val actionService: ActionService,
                       @Autowired private val session: PlayerSession) {

    /**
     * SimpleAction handling
     * @param action SimpleAction
     * @return ResponseEntity<Any>:
     *      - 200 if action successfully handled
     *      - 400 if action is invalid
     *      - 401 if httpSession is missed
     *      - 555 if db fails
     */
    @PostMapping("/performAction")
    fun performAction(@RequestBody action: SimpleAction): ResponseEntity<Any> {

        session.playerName?.let { name ->
            session.bud?.let {
                return try {
                    val actionResult = actionService.performSimpleAction(name, it, action)
                    if (actionResult.finished)
                        session.bud = null
                    ResponseEntity.ok(actionResult)
                } catch (e: ValidationException) {
                    ResponseEntity.badRequest().body(e.message)
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}