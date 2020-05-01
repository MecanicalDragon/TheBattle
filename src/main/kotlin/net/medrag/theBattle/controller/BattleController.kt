package net.medrag.theBattle.controller

import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.BattlePageResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.service.BattleService
import net.medrag.theBattle.service.PlayerSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@RestController
@RequestMapping("/battle")
class BattleController(@Autowired private val battleService: BattleService,
                       @Autowired private val session: PlayerSession) {

    /**
     * Registers battle bid.
     * Generates and attaches 'BATTLE_UUID' parameter to the session.
     * @param squadDTO SquadDTO - squad, that goes into battle
     * @return ResponseEntity<BattleBidResponse>:
     *      - 200 if battle bid have been registered
     *      - 400 if request is invalid
     *      - 401 if there are no player name in session
     *      - 555 if database problem occurs
     */
    @PostMapping("/registerBattleBid")
    fun registerBattleBid(@RequestBody squadDTO: SquadDTO): ResponseEntity<BattleBidResponse> {

        session.playerName?.let {
            session.bud = null
            return try {
                val resp = battleService.registerBattleBid(it, squadDTO)
                session.playerStatus = PlayerStatus.IN_SEARCH
                ResponseEntity.ok(resp)
            } catch (e: ValidationException) {
                ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }


    /**
     * Cancels battle bid
     * @return ResponseEntity<String>:
     *      - 200 if cancelled
     *      - 230 if battle has started already
     *      - 401 if player is unrecognized
     *      - 555 if database fails
     */
    @PostMapping("/cancelBid")
    fun cancelBid(): ResponseEntity<Void> {

        session.playerName?.let { name ->
            session.playerId?.let {
                try {
                    battleService.cancelBid(it, name)
                    session.bud = null
                    session.playerStatus = PlayerStatus.FREE
                    return ResponseEntity.status(200).build()
                } catch (e: ProcessingException) {
                    return ResponseEntity.status(230).build()
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }


    /**
     * Request of FoesPair. Been called
     * @return ResponseEntity<FoesPair>:
     *      - 200 if request handled correctly
     *      - 400 if there is no battle uuid in session or database
     *      - 401 if there is no session
     *      - 555 if database fails
     */
    @GetMapping("/getDislocations")
    fun getDislocations(): ResponseEntity<BattlePageResponse> {

        session.playerName?.let { playerName ->

            // Common request.
            session.bud?.let {
                return try {
                    ResponseEntity.ok(battleService.getDislocationsOnBattleStart(playerName, it))
                } catch (e: ValidationException) {
                    session.bud = null
                    ResponseEntity.badRequest().build()
                }
            }

            // 3 cases, if player does not know his bud:
            // 1. The battle just started, and it's a first request.
            // 2. The battle has finished.
            battleService.getBud(playerName)?.let {
                val bud = UUID.fromString(it)
                return try {
                    val dislocations = battleService.getDislocationsOnBattleStart(playerName, bud)
                    session.bud = bud
                    session.playerStatus = PlayerStatus.IN_BATTLE
                    ResponseEntity.ok(dislocations)
                } catch (e: ValidationException) {
                    ResponseEntity.badRequest().build()
                }
            }
            return ResponseEntity.badRequest().build()
        }
        // 2. Invalid request.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}