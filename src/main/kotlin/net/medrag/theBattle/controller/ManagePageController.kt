package net.medrag.theBattle.controller

import net.medrag.theBattle.model.IncompatibleDataException
import net.medrag.theBattle.model.PlayerSession
import net.medrag.theBattle.model.RETIRED
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.ManagePageResponse
import net.medrag.theBattle.service.api.PlayerServiceApi
import net.medrag.theBattle.service.api.UnitServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


/**
 * Handles squad manage page requests.
 * @author Stanislav Tretyakov
 * 19.12.2019
 */
@RestController
@RequestMapping("/squad")
class ManagePageController(@Autowired private val unitService: UnitServiceApi,
                           @Autowired private val playerService: PlayerServiceApi,
                           @Autowired private val session: PlayerSession) {

    /**
     * Returns free heroes pool and player data
     * @return ResponseEntity<Any>:
     *      - 200 List of UnitDTO
     *      - 401 Error string
     *      - 555 if db fails
     */
    @GetMapping("/getPoolAndData")
    fun getPoolAndData(): ResponseEntity<Any> {

        session.playerName?.let {
            try {
                val pool = unitService.getPool(it)
                val player = playerService.getPlayerData(it)
                session.playerStatus = player.status
                return ResponseEntity.ok(ManagePageResponse(player, pool))
            } catch (e: IncompatibleDataException) {
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Adds new hero to player's pool
     * @param name String - new unit name
     * @param type Type - unit type
     * @return ResponseEntity<Any>:
     *      - 200 if everything is OK
     *      - 400 if user is absent in database
     *      - 401 if user is not registered
     *      - 412 if new unit name does not match regex pattern
     *      - 555 if db fails
     */
    @PostMapping("/addNew")
    fun addNew(@RequestParam name: String,
               @RequestParam type: Unitt.Unit.Type): ResponseEntity<Any> {

        session.playerName?.let {
            return try {
                ResponseEntity.ok(unitService.addNewUnit(it, name, type))
            } catch (e: ValidationException) {
                ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build<Any>()
            } catch (e: IncompatibleDataException) {
                ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Removes unit from player's pool
     * @param unit Long - unit id
     * @return ResponseEntity<String>:
     *      - 200 if OK
     *      - 400 if smth went wrong
     *      - 401 if no session
     *      - 555 if db fails
     */
    @DeleteMapping("/retireHero")
    fun delete(@RequestParam unit: Long): ResponseEntity<String> {

        session.playerName?.let {
            return try {
                unitService.deleteUnit(unit, it)
                ResponseEntity.ok(RETIRED)
            } catch (e: IncompatibleDataException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}