package net.medrag.theBattle.controller

import net.medrag.theBattle.model.PLAYER_SESSION
import net.medrag.theBattle.model.classes.Archer
import net.medrag.theBattle.model.classes.Mage
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.classes.Warrior
import net.medrag.theBattle.service.SquadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@RestController
@RequestMapping("/squad")
class SquadController(@Autowired val squadService: SquadService) {

    @GetMapping("/getNewSquad")
    fun getSquad() = squadService.getSquad()

    @GetMapping("/getPool")
    fun getPool(request: HttpServletRequest): List<Unitt> {
//        val playerName: String = request.getSession(false).getAttribute(PLAYER_SESSION) as String
//        return squadService.getPool(playerName)

//        return squadService.getPool("asdd")
        return listOf(Warrior(), Mage(), Archer(), Warrior())
    }


}