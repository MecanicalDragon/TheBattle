package net.medrag.theBattle.controller

import net.medrag.theBattle.model.AWAIT
import net.medrag.theBattle.model.CANCELLED
import net.medrag.theBattle.model.NOT_CANCELLED
import net.medrag.theBattle.model.START
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.DislocationDTO
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

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @PostMapping("/startBattleBid")
    fun startBattleBid(@RequestBody squadDTO: SquadDTO,
                       @RequestParam(required = false) pName: String?,
                       request: HttpServletRequest): ResponseEntity<BattleBidResponse> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        val uuid = battleService.startBattleBid(playerName, squadDTO)
        return ResponseEntity.ok(BattleBidResponse(if (uuid == null) AWAIT else START, uuid))
    }

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @PostMapping("/cancelBid")
    fun cancelBid(@RequestParam(required = false) pName: String?,
                  request: HttpServletRequest): ResponseEntity<String> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(if (battleService.cancelBid(playerName)) CANCELLED else NOT_CANCELLED)
    }

    @GetMapping("/getDislocations")
    fun getDislocations(@RequestParam(required = false) pName: String?, @RequestParam bud: String,
                        request: HttpServletRequest): ResponseEntity<FoesPair> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(battleService.getDislocations(playerName, bud))
    }

    //TODO: remove after tests
    @PostMapping("/test")
    fun test(@RequestParam(required = false) pName: String?,
             request: HttpServletRequest): ResponseEntity<BattleBidResponse> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        val uuid = battleService.test(playerName)
        return ResponseEntity.ok(BattleBidResponse(AWAIT, uuid))
    }
}