package net.medrag.theBattle.controller

import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@RestController
@RequestMapping("/squad")
class SquadController(@Autowired val squadService: SquadService) {

    @GetMapping("/getNewSquad")
    fun getSquad() = squadService.getRandomSquad()

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @GetMapping("/getPool")
    fun getPool(@RequestParam(required = false) pName: String?, request: HttpServletRequest): ResponseEntity<List<UnitDTO>> {

        if (pName != null && !pName.isEmpty()) {
            println("no session request")
            return ResponseEntity.ok(squadService.getPool(pName))
        }

        val playerName: String = request.getSession(false).getAttribute(PLAYER_SESSION) as String
        return ResponseEntity.ok(squadService.getPool(playerName))
    }

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @PostMapping("/addNew")
    fun addNew(@RequestParam(required = false) pName: String?,
               @RequestParam name: String,
               @RequestParam type: Unitt.Unit.Type,
               request: HttpServletRequest): ResponseEntity<UnitDTO> {

        try {
            if (pName != null && !pName.isEmpty()) {
                println("no session request")
                return ResponseEntity.ok(squadService.addNewUnit(pName, name, type))
            }

            val playerName: String = request.getSession(false).getAttribute(PLAYER_SESSION) as String
            return ResponseEntity.ok(squadService.addNewUnit(playerName, name, type))

        } catch (e: ValidationException) {
            return ResponseEntity.badRequest().build()
        }
    }
}