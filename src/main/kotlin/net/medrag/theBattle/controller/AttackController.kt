package net.medrag.theBattle.controller

import net.medrag.theBattle.model.classes.ValidatedSquad
import net.medrag.theBattle.model.dto.ActionResult
import net.medrag.theBattle.model.dto.AttackAction
import net.medrag.theBattle.service.AttackService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 15.01.2020
 */
@RestController
@RequestMapping("/attack")
class AttackController(private val attackService: AttackService) {

    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    @PostMapping("/performAttack")
    fun performAttack(@RequestParam pName: String?,
                      @RequestBody attackAction: AttackAction,
                      request: HttpServletRequest): ResponseEntity<ActionResult> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        extractBattleUUID(request, playerName)?.let {
            return ResponseEntity.ok(attackService.performAttack(playerName, it, attackAction))
        }
        return ResponseEntity.badRequest().build()
    }
}