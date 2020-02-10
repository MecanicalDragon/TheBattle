package net.medrag.theBattle.controller

import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.RETIRED
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@RestController
@RequestMapping("/squad")
class SquadController(@Autowired val squadService: SquadService) {

    @GetMapping("/getPool")
    fun getPool(session: HttpSession): ResponseEntity<List<UnitDTO>> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            return ResponseEntity.ok(squadService.getPool(it))
        }
        return ResponseEntity.badRequest().build()
    }

    @PostMapping("/addNew")
    fun addNew(@RequestParam name: String,
               @RequestParam type: Unitt.Unit.Type,
               session: HttpSession): ResponseEntity<UnitDTO> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            try {
                return ResponseEntity.ok(squadService.addNewUnit(it, name, type))
            } catch (e: ValidationException) {
            }
        }
        return ResponseEntity.badRequest().build()
    }

    @DeleteMapping("/retireHero")
    fun delete(@RequestParam unit: Long, session: HttpSession): ResponseEntity<String> {

        (session.getAttribute(PLAYER_SESSION) as? String)?.let {
            return try {
                squadService.deleteUnit(unit, it)
                ResponseEntity.ok(RETIRED)
            } catch (e: ValidationException) {
                ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
            }
        }
        return ResponseEntity("Could not extract player's name", HttpStatus.BAD_REQUEST)
    }
}