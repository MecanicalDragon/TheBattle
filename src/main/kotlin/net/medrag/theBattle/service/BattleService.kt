package net.medrag.theBattle.service

import kotlinx.coroutines.*
import net.medrag.theBattle.model.AWAIT
import net.medrag.theBattle.model.GAME_FOUND
import net.medrag.theBattle.model.START
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.ValidatedSquad
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.dto.buildUnit
import net.medrag.theBattle.model.entities.UnitStatus
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
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
        @Autowired val playerRepo: PlayerRepo,
        @Autowired val unitRepo: UnitRepo,
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
     * Map stores players, that were notified about battle start by websocket and do not know their Battle UUID
     */
    //TODO: why don't you add this info to player entity?
    private val playersWhoDontKnowTheirBattleUUIDs = ConcurrentHashMap<String, UUID>()

    /**
     * Receives battle bid from the player with name 'playerName'.
     * 'squadDTO' - suggested squad for the battle.
     * Function validates if squadDTO contains only player's ready for battle heroes and throws ValidationException if not.
     * Then function tries to obtain another ready for battle squad from the queue. If it exists there, function creates
     * 'FoesPair' with battle id of extracted bid, notifies foe with websockets and returns battle id with status START.
     * Otherwise new bid will be added to the searching queue with new battle uuid, it will be returned with status AWAIT.
     */
    @Transactional(readOnly = true)
    fun registerBattleBid(playerName: String, squadDTO: SquadDTO): BattleBidResponse {
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
        for (unit in listOf(unit1, unit2, unit3, unit4, unit5)) {
            if (unit.status != UnitStatus.IN_POOL)
                throw ValidationException("Someone cheats: $unit is not free fo pick!")
        }

        searching.poll()?.let {
            val uuid = UUID.randomUUID()
            val squad = ValidatedSquad(player.name, squadDTO.type, buildUnit(unit1), buildUnit(unit2),
                    buildUnit(unit3), buildUnit(unit4), buildUnit(unit5))
            val pair = FoesPair(it, squad)
            battleFoes[uuid] = pair
            playersWhoDontKnowTheirBattleUUIDs[playerName] = uuid
            playersWhoDontKnowTheirBattleUUIDs[it.playerName] = uuid
            wSocket.convertAndSend("/searching/${it.playerName}", GAME_FOUND)
            return BattleBidResponse(START)
        }

        searching.add(ValidatedSquad(player.name, squadDTO.type, buildUnit(unit1), buildUnit(unit2),
                buildUnit(unit3), buildUnit(unit4), buildUnit(unit5)))
        return BattleBidResponse(AWAIT)
    }

    /**
     * Removes squad from the searching queue. Returns true if succeeds, otherwise false.
     */
    fun cancelBid(playerName: String) = searching.remove(ValidatedSquad(playerName))

    /**
     * Removes player's battle uuid from playersWhoDontKnowTheirBattleUUIDs and returns it
     */
    fun getBud(playerName: String) = playersWhoDontKnowTheirBattleUUIDs.remove(playerName)

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

//    fun updateDislocations(playerName: String, bud: UUID, pair: FoesPair) {
//        battleFoes[bud]?.let {
//            if (it.foe1.playerName == playerName || it.foe2.playerName == playerName)
//                battleFoes[bud] = pair
//            else throw ValidationException("Your name is not in battle data, cheater.")
//        }
//        throw ValidationException("Your battle id is not in battle data, cheater.")
//    }

    //TODO: delete after tests
    fun testWebSockets(playerName: String): UUID {
        println("received")
        GlobalScope.launch {
            println("wait...")
            delay(5000)
            println("now!")
            wSocket.convertAndSend("/searching/$playerName", "$GAME_FOUND->${UUID.randomUUID()}")
            println("sent")
        }
        return UUID.randomUUID()
    }
}