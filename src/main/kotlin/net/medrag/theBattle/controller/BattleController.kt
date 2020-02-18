package net.medrag.theBattle.controller

import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.service.BattleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
     * Generates and attaches 'BATTLE_UUID' parameter to the session.
     * @param squadDTO SquadDTO - squad, that goes into battle
     * @param session HttpSession
     * @return ResponseEntity<BattleBidResponse>:
     *      - 200 if battle bid have been registered
     *      - 400 if request is invalid
     *      - 401 if there are no player name in session
     *      - 555 if database problem occurs
     */
    @PostMapping("/registerBattleBid")
    fun registerBattleBid(@RequestBody squadDTO: SquadDTO, session: HttpSession): ResponseEntity<BattleBidResponse> {

        (session.getAttribute(PLAYER_NAME) as? String)?.let {
            session.removeAttribute(BATTLE_UUID)
            return try {
                val resp = battleService.registerBattleBid(it, squadDTO)
                session.setAttribute(PLAYER_STATUS, PlayerStatus.IN_SEARCH)
                ResponseEntity.ok(resp)
            } catch (e: ValidationException) {
                ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }


    /**
     * Cancels battle bid
     * @param session HttpSession
     * @return ResponseEntity<String>:
     *      - 200 if ok (Cancelled or not)
     *      - 401 if player is unrecognized
     *      - 555 if database fails
     */
    @PostMapping("/cancelBid")
    fun cancelBid(session: HttpSession): ResponseEntity<String> {

        (session.getAttribute(PLAYER_NAME) as? String)?.let {
            try {
                battleService.cancelBid(it)
                session.removeAttribute(BATTLE_UUID)
            } catch (e: ProcessingException) {
                return ResponseEntity.ok(NOT_CANCELLED)
            }
            session.setAttribute(PLAYER_STATUS, PlayerStatus.FREE)
            return ResponseEntity.ok(CANCELLED)
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }


    /**
     * Request of FoesPair. Been called
     * @param session HttpSession
     * @return ResponseEntity<FoesPair>:
     *      - 200 if request handled correctly
     *      - 400 if there is no battle uuid in session or database
     *      - 401 if there is no session
     *      - 555 if database fails
     */
    @GetMapping("/getDislocations")
    fun getDislocations(session: HttpSession): ResponseEntity<FoesPair> {

        (session.getAttribute(PLAYER_NAME) as? String)?.let { playerName ->

            // Common request.
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
                session.setAttribute(PLAYER_STATUS, PlayerStatus.IN_BATTLE)
                return ResponseEntity.ok(battleService.getDislocations(playerName, bud))
            }
            return ResponseEntity.badRequest().build()
        }
        // 2. Invalid request.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}