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
    @PostMapping("/startBattleBid")
    fun startBattleBid(@RequestBody squadDTO: SquadDTO,
                       @RequestParam(required = false) pName: String?,
                       request: HttpServletRequest): ResponseEntity<BattleBidResponse> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        val resp = battleService.startBattleBid(playerName, squadDTO)
        request.getSession(false)?.setAttribute(BATTLE_UUID, resp.uuid)
        return ResponseEntity.ok(resp)
    }

    /**
     * Cancels battle bid.
     * Sets 'BATTLE_UUID' parameter to null.
     */
    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    //TODO: TODO_SECURITY: requestParam 'bud' should be removed in release
    @PostMapping("/cancelBid")
    fun cancelBid(@RequestParam(required = false) pName: String?, @RequestParam(required = false) bud: String?,
                  request: HttpServletRequest): ResponseEntity<String> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        val uuid = extractBattleUUID(request, bud)
        val cancelled = battleService.cancelBid(playerName, uuid)
        if (cancelled) request.getSession(false)?.setAttribute(BATTLE_UUID, null)
        return ResponseEntity.ok(if (cancelled) CANCELLED else NOT_CANCELLED)
    }

    /**
     * Returns foesPair.
     */
    //TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
    //TODO: TODO_SECURITY: requestParam 'bud' should be removed in release
    @GetMapping("/getDislocations")
    fun getDislocations(@RequestParam(required = false) pName: String?, @RequestParam bud: String?,
                        request: HttpServletRequest): ResponseEntity<FoesPair> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()

        val uuid = extractBattleUUID(request, bud)
        return ResponseEntity.ok(battleService.getDislocations(playerName, uuid))
    }

    //TODO: remove after tests
    @PostMapping("/test")
    fun test(@RequestParam(required = false) pName: String?,
             request: HttpServletRequest): ResponseEntity<BattleBidResponse> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        val uuid = battleService.testWebSockets(playerName)
        return ResponseEntity.ok(BattleBidResponse(AWAIT, uuid))
    }
}