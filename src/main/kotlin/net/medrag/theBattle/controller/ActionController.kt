package net.medrag.theBattle.controller

import net.medrag.theBattle.model.ProcessingException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.SimpleAction
import net.medrag.theBattle.model.PlayerSession
import net.medrag.theBattle.service.api.ActionServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


/**
 * @author Stanislav Tretyakov
 * 15.01.2020
 *
 * Controller for handling battle actions
 */
@RestController
@RequestMapping("/action")
class ActionController(@Autowired private val actionService: ActionServiceApi,
                       @Autowired private val session: PlayerSession) {

    /**
     * SimpleAction handling
     * @param action SimpleAction
     * @return ResponseEntity<Any>:
     *      - 200 if action successfully handled
     *      - 230 if another player already acts
     *      - 400 if action is invalid
     *      - 401 if httpSession is missed
     *      - 555 if db fails
     */
    @PostMapping("/performAction")
    fun performAction(@RequestBody action: SimpleAction): ResponseEntity<Any> {

        session.playerName?.let { name ->
            session.bud?.let {
                return try {
                    ResponseEntity.ok(actionService.performSimpleAction(name, it, action))
                } catch (e: ValidationException) {
                    ResponseEntity.badRequest().body(e.message)
                } catch (e: ProcessingException) {
                    ResponseEntity.status(230).body(Unit)
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Endpoint is triggered by client if turn time exceeds limit.
     */
    @PostMapping("/pingTurn")
    fun pingTurn() {
        session.playerName?.let {
            session.bud?.let { bud ->
                actionService.pingTurn(it, bud)
            }
        }
    }
}