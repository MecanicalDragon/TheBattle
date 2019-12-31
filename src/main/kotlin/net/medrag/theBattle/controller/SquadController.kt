package net.medrag.theBattle.controller

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @GetMapping("/getPool")
    fun getPool(@RequestParam(required = false) pName: String?, request: HttpServletRequest): ResponseEntity<List<UnitDTO>> {
        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(squadService.getPool(playerName))
    }

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @PostMapping("/addNew")
    fun addNew(@RequestParam(required = false) pName: String?,
               @RequestParam name: String,
               @RequestParam type: Unitt.Unit.Type,
               request: HttpServletRequest): ResponseEntity<UnitDTO> {

        try {
            val playerName = extractPlayerName(request, pName)
            if (playerName.isNullOrBlank()) return ResponseEntity.badRequest().build()
            return ResponseEntity.ok(squadService.addNewUnit(playerName, name, type))

        } catch (e: ValidationException) {
            return ResponseEntity.badRequest().build()
        }
    }

    //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
    @DeleteMapping("/retireHero")
    fun delete(@RequestParam unit: Long,
               @RequestParam(required = false) pName: String?,
               request: HttpServletRequest): ResponseEntity<String> {

        val playerName = extractPlayerName(request, pName)
        if (playerName.isNullOrBlank())
            return ResponseEntity("Could not extract player's name", HttpStatus.BAD_REQUEST)
        try {
            squadService.deleteUnit(unit, playerName)
        } catch (e: ValidationException) {
            return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity.ok("RETIRED")
    }
}