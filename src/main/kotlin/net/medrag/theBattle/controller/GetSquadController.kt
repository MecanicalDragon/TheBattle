package net.medrag.theBattle.controller

import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@RestController
@RequestMapping("/squad")
class GetSquadController(@Autowired val squadService: SquadService) {

    @GetMapping("/getNewSquad")
    fun getSquad() = squadService.getSquad()

}