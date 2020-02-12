package net.medrag.theBattle.controller

import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.service.BattleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpSession


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
     * @param squadDTO SquadDTO - squad, that goes into battle
     * @param session HttpSession
     * @return ResponseEntity<BattleBidResponse>:
     *      - 200 if battle bid have been registered
     *      - 400 if there are no player name in session
     */
    @PostMapping("/registerBattleBid")
    fun registerBattleBid(@RequestBody squadDTO: SquadDTO, session: HttpSession): ResponseEntity<BattleBidResponse> {

        //TODO: handle 555 everywhere
        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            session.removeAttribute(BATTLE_UUID)
            return try {
                val resp = battleService.registerBattleBid(it, squadDTO)
                ResponseEntity.ok(resp)
            } catch (e: ValidationException) {
                ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.badRequest().build()
    }

    /**
     * Cancels battle bid.
     * Sets 'BATTLE_UUID' parameter to null.
     */
    @PostMapping("/cancelBid")
    fun cancelBid(session: HttpSession): ResponseEntity<String> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            val cancelled = battleService.cancelBid(it)
            if (cancelled) {
                session.removeAttribute(BATTLE_UUID)
                session.removeAttribute(IN_SEARCH)
            }
            return ResponseEntity.ok(if (cancelled) CANCELLED else NOT_CANCELLED)
        }
        return ResponseEntity.badRequest().build()
    }

    /**
     * Returns foesPair.
     */
    @GetMapping("/getDislocations")
    fun getDislocations(session: HttpSession): ResponseEntity<FoesPair> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let { playerName ->
            (session.getAttribute(BATTLE_UUID) as? UUID)?.let {
                return try {
                    ResponseEntity.ok(battleService.getDislocations(playerName, it))
                } catch (e: ValidationException) {
                    session.removeAttribute(BATTLE_UUID)
                    ResponseEntity.badRequest().build()
                }
            }

            // 2 cases, if player does not know his bud:
            // 1. The battle just started, and it's a first request.
            battleService.getBud(playerName)?.let {
                val bud = UUID.fromString(it)
                session.setAttribute(BATTLE_UUID, bud)
                return ResponseEntity.ok(battleService.getDislocations(playerName, bud))
            }
        }
        // 2. Invalid request.
        return ResponseEntity.badRequest().build()
    }
}