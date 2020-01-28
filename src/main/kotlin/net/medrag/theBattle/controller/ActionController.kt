package net.medrag.theBattle.controller

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.SimpleAction
import net.medrag.theBattle.model.dto.ActionResult
import net.medrag.theBattle.model.dto.AttackAction
import net.medrag.theBattle.service.ActionService
import net.medrag.theBattle.service.AttackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 15.01.2020
 */
@RestController
@RequestMapping("/action")
class ActionController(private val actionService: ActionService) {

    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    @PostMapping("/performAction")
    fun performAction(@RequestParam pName: String?,
                      @RequestBody action: SimpleAction,
                      request: HttpServletRequest): ResponseEntity<ActionResult> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        extractBattleUUID(request, playerName)?.let {
            try {
                val actionResult = actionService.performSimpleAction(playerName, it, action)
                if (actionResult.finished)
                    invalidateBattleUUID(request, playerName)
                return ResponseEntity.ok(actionResult)
            } catch (e: ValidationException) {
                return ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.badRequest().build()
    }
}