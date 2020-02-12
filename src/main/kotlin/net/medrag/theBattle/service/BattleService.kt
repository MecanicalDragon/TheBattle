package net.medrag.theBattle.service

import net.medrag.theBattle.model.AWAIT
import net.medrag.theBattle.model.GAME_FOUND
import net.medrag.theBattle.model.START
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.squad.ValidatedSquad
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.buildUnit
import net.medrag.theBattle.model.entities.PlayerStatus
import net.medrag.theBattle.model.entities.UnitStatus
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@Service
class BattleService(
        @Autowired private val playerRepo: PlayerRepo,
        @Autowired private val unitRepo: UnitRepo,
        @Autowired private val wSocket: SimpMessagingTemplate) {

    /**
     * Concurrent queue with squads, ready for battle
     */
    private val searching = ConcurrentLinkedQueue<ValidatedSquad>()

    /**
     * Map of ongoing battles.
     */
    private val battleFoes = ConcurrentHashMap<UUID, FoesPair>()

    /**
     * Receives battle bid from the player with name 'playerName'.
     * Function validates if squadDTO contains only player's ready for battle heroes and throws ValidationException if not.
     * Then function tries to obtain another ready for battle squad from the queue. If it exists there, function creates
     * 'FoesPair' with battle id of extracted bid, notifies foe with websockets and returns battle id with status START.
     * Otherwise new bid will be added to the searching queue with new battle uuid, it will be returned with status AWAIT.
     * @param playerName String - who's bid is it
     * @param squadDTO SquadDTO - suggested squad for the battle.
     * @return BattleBidResponse - AWAIT or START
     * @throws ValidationException if squad or player data is incorrect
     */
    @Throws(ValidationException::class)
    @Transactional(readOnly = true)
    fun registerBattleBid(playerName: String, squadDTO: SquadDTO): BattleBidResponse {

        val ids = setOf(squadDTO.pos1.id, squadDTO.pos2.id, squadDTO.pos3.id, squadDTO.pos4.id, squadDTO.pos5.id)
        val units = unitRepo.findAllByPlayer_NameAndIdIn(playerName, ids)
        if (units.size != 5) throw ValidationException("Squad should contain 5 members, yet $playerName has ${units.size} in declared squad.")

        val unit1 = units.find { squadDTO.pos1.id == it.id }
        val unit2 = units.find { squadDTO.pos2.id == it.id }
        val unit3 = units.find { squadDTO.pos3.id == it.id }
        val unit4 = units.find { squadDTO.pos4.id == it.id }
        val unit5 = units.find { squadDTO.pos5.id == it.id }

        if (unit1 == null || unit2 == null || unit3 == null || unit4 == null || unit5 == null) {
            throw ValidationException("Someone cheats: there are no such heroes in your pool!")
        }
        for (unit in listOf(unit1, unit2, unit3, unit4, unit5)) {
            if (unit.status != UnitStatus.IN_POOL)
                throw ValidationException("Someone cheats: $unit is not free fo pick!")
        }

        val squad = ValidatedSquad(playerName, squadDTO.type, buildUnit(unit1), buildUnit(unit2),
                buildUnit(unit3), buildUnit(unit4), buildUnit(unit5))

        searching.poll()?.let {
            val uuid = UUID.randomUUID()

            unitRepo.setInStatus(UnitStatus.IN_BATTLE, ids + setOf(it.pos1.id, it.pos2.id, it.pos3.id, it.pos4.id, it.pos5.id))
            playerRepo.setStatusAndUUID(PlayerStatus.IN_BATTLE, uuid.toString(), listOf(playerName, it.playerName))

            battleFoes[uuid] = FoesPair(it, squad)
            wSocket.convertAndSend("/searching/${it.playerName}", GAME_FOUND)
            return BattleBidResponse(START)
        }

        unitRepo.setInStatus(UnitStatus.IN_SEARCH, ids)
        playerRepo.setStatusAndUUID(PlayerStatus.IN_SEARCH, null, listOf(playerName))

        searching.add(squad)
        return BattleBidResponse(AWAIT)
    }

    @Transactional
    fun finishTheBattle(uuid: UUID, winner: ValidatedSquad, looser: ValidatedSquad, actionUnit: UnitDTO) {
        giveExperience(winner, looser, actionUnit)
        playerRepo.incrementGamesCount(looser.playerName)
        playerRepo.incrementWinsAndGamesCount(winner.playerName)
        battleFoes.remove(uuid)
    }

    @Transactional
    fun giveExperience(winner: ValidatedSquad, looser: ValidatedSquad, finalAttack: UnitDTO) {
        if (looser.dead == 5) {

            val wonUnits = winner.map.values
            val loosedUnits = looser.map.values

            val expForWinner = loosedUnits.asSequence().fold(0) { i, u -> i + (u.type.basicReward * u.level / 2) }
            val expForLooser = wonUnits.asSequence().filter { it.hp == 0 }.fold(0) { i, u -> i + (u.type.basicReward * u.level / 2) }

            val xps = HashMap<Long, Int>()
            wonUnits.forEach {
                if (it.hp == 0) xps[it.id] = expForWinner / 3 * 2
                else xps[it.id] = expForWinner
            }
            loosedUnits.forEach {
                xps[it.id] = expForLooser / 2
            }
            xps[finalAttack.id] = (xps[finalAttack.id] ?: 0 + (expForWinner / 10))
            val unitsToReward = unitRepo.findAllByIdIn((wonUnits + loosedUnits).map { it.id })

            for (unit in unitsToReward) {
                unit.experience += xps[unit.id] ?: 0
            }
            unitRepo.saveAll(unitsToReward)
        }
    }

    /**
     * Removes squad from the searching queue. Returns true if succeeds, otherwise false.
     */
    fun cancelBid(playerName: String) = searching.remove(ValidatedSquad(playerName))

    /**
     * Returns player's battle uuid
     */
    fun getBud(playerName: String) = playerRepo.getBud(playerName)

    /**
     * Returns foesPair, based on Battle UUID
     */
    fun getDislocations(playerName: String, bud: UUID): FoesPair {
        battleFoes[bud]?.let {
            if (it.foe1.playerName == playerName || it.foe2.playerName == playerName) return it
            throw ValidationException("Your name is not in battle data, cheater.")
        }
        throw ValidationException("Your battle id is not in battle data, cheater.")
    }
}