package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Fighter
import net.medrag.theBattle.model.classes.Squad
import net.medrag.theBattle.model.dto.DislocationDTO
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.buildUnit
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@Service
class BattleService(
        @Autowired val playerRepo: PlayerRepo,
        @Autowired val unitRepo: UnitRepo,
        @Autowired val battleSearchingService: BattleSearchingService) {

    @Transactional(readOnly = true)
    fun startBattleBid(playerName: String, squadDTO: SquadDTO): UUID? {
        val player = playerRepo.findByName(playerName)
                ?: throw ValidationException("Player with this name does not exists.")
        val unit1 = unitRepo.findByIdAndPlayer(squadDTO.pos1.id, player)
        val unit2 = unitRepo.findByIdAndPlayer(squadDTO.pos2.id, player)
        val unit3 = unitRepo.findByIdAndPlayer(squadDTO.pos3.id, player)
        val unit4 = unitRepo.findByIdAndPlayer(squadDTO.pos4.id, player)
        val unit5 = unitRepo.findByIdAndPlayer(squadDTO.pos5.id, player)

        if (unit1 == null || unit2 == null || unit3 == null || unit4 == null || unit5 == null) {
            throw ValidationException("Someone cheats: there are no such heroes in your pool!")
        }
        val squad = Squad(player, squadDTO.type, buildUnit(unit1), buildUnit(unit2), buildUnit(unit3), buildUnit(unit4), buildUnit(unit5))
        return battleSearchingService.getFoeOrAddToQueue(squad)
    }

    fun test(playerName: String): UUID? {
        battleSearchingService.test()
        return null
    }

    fun cancelBid(playerName: String): Boolean {
        val player = playerRepo.findByName(playerName)
                ?: throw ValidationException("Player with this name does not exists.")
        val squad = Squad(player, SquadType.FORCED_FRONT, Squad.mock, Squad.mock, Squad.mock, Squad.mock, Squad.mock)
        return battleSearchingService.cancelBid(squad)
    }

    //TODO: are you really gonna convey player's personal data to the opponent?
    //TODO: are you really gonna convey player's personal data even without DTO?
    fun getDislocations(playerName: String, bud: String): FoesPair {
        val foesPair = battleSearchingService.getBattleByUuid(bud)
        if (foesPair.foe1.player.name == playerName || foesPair.foe2.player.name == playerName) return foesPair
        else throw ValidationException("Your name is not in battle data, cheater.")
    }
}