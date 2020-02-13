package net.medrag.theBattle.controller

import net.medrag.theBattle.model.*
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 * Processes squad manage page requests
 */
@RestController
@RequestMapping("/squad")
class SquadController(@Autowired val squadService: SquadService) {

    /**
     * Returns free heroes pool
     * @param session HttpSession
     * @return ResponseEntity<Any>:
     *      - 200 List of UnitDTO
     *      - 401 Error string
     *      - 555 if db fails
     */
    @GetMapping("/getPool")
    fun getPool(session: HttpSession): ResponseEntity<Any> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            return ResponseEntity.ok(squadService.getPool(it))
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Adds new hero to player's pool
     * @param name String - new unit name
     * @param type Type - unit type
     * @param session HttpSession - player's session
     * @return ResponseEntity<Any>:
     *      - 200 if everything is OK
     *      - 400 if user is absent in database
     *      - 401 if user is not registered
     *      - 412 if new unit name does not match regex pattern
     *      - 555 if db fails
     */
    @PostMapping("/addNew")
    fun addNew(@RequestParam name: String,
               @RequestParam type: Unitt.Unit.Type,
               session: HttpSession): ResponseEntity<Any> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            return try {
                ResponseEntity.ok(squadService.addNewUnit(it, name, type))
            } catch (e: ValidationException) {
                ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build<Any>()
            } catch (e: IncompatibleDataException){
                ResponseEntity.badRequest().build()
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    /**
     * Removes unit from player's pool
     * @param unit Long - unit id
     * @param session HttpSession - player's session
     * @return ResponseEntity<String>:
     *      - 200 if OK
     *      - 400 if smth went wrong
     *      - 401 if no session
     *      - 555 if db fails
     */
    @DeleteMapping("/retireHero")
    fun delete(@RequestParam unit: Long, session: HttpSession): ResponseEntity<String> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            return try {
                squadService.deleteUnit(unit, it)
                ResponseEntity.ok(RETIRED)
            } catch (e: IncompatibleDataException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}