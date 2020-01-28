package net.medrag.theBattle.controller

import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.service.BattleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@RestController
@RequestMapping("/battle")
class BattleController(@Autowired val battleService: BattleService) {

    /**
     * Registers battle bid.
     * Generates and sets 'BATTLE_UUID' parameter to the session.
     */
    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    @PostMapping("/registerBattleBid")
    fun registerBattleBid(@RequestBody squadDTO: SquadDTO,
                          @RequestParam(required = false) pName: String?,
                          request: HttpServletRequest): ResponseEntity<BattleBidResponse> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        invalidateBattleUUID(request, playerName)
        val resp = battleService.registerBattleBid(playerName, squadDTO)
        return ResponseEntity.ok(resp)
    }

    /**
     * Cancels battle bid.
     * Sets 'BATTLE_UUID' parameter to null.
     */
    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    @PostMapping("/cancelBid")
    fun cancelBid(@RequestParam(required = false) pName: String?, request: HttpServletRequest): ResponseEntity<String> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        val cancelled = battleService.cancelBid(playerName)
        if (cancelled) request.getSession(false)?.setAttribute(BATTLE_UUID, null)
        return ResponseEntity.ok(if (cancelled) CANCELLED else NOT_CANCELLED)
    }

    /**
     * Returns foesPair.
     */
    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    @GetMapping("/getDislocations")
    fun getDislocations(@RequestParam(required = false) pName: String?,
                        request: HttpServletRequest): ResponseEntity<FoesPair> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        extractBattleUUID(request, playerName)?.let {
            try {
                return ResponseEntity.ok(battleService.getDislocations(playerName, it))
            } catch (e: ValidationException) {
                invalidateBattleUUID(request, pName)
                return ResponseEntity.badRequest().build()
            }
        }
        battleService.getBud(playerName)?.let {
            emulateBudSetting(playerName, it, request)
            return ResponseEntity.ok(battleService.getDislocations(playerName, it))
        }
        return ResponseEntity.badRequest().build()
    }
}