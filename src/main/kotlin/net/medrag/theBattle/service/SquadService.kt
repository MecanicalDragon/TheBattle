package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.dto.buildUnit
import net.medrag.theBattle.model.dto.buildUnitEntity
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.squad.Squad
import net.medrag.theBattle.repo.PlayerRepo
import net.medrag.theBattle.repo.UnitRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@Service
class SquadService(@Autowired val unitRepo: UnitRepo, @Autowired val playerRepo: PlayerRepo) {

    /**
     * Get random squad
     */
    fun getRandomSquad(): Squad {
        val squad = Squad();
//        squad.type = SquadType.values()[(Math.random() * SquadType.values().size).toInt()]
//        squad.pos1 = Unitt.Unit.Type.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
//        squad.pos2 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
//        squad.pos3 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
//        squad.pos4 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
//        squad.pos5 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        return squad
    }

    /**
     * Returns Player's hero pool or empty list
     */
    fun getPool(playerName: String): List<UnitDTO> {
//        val list = playerRepo.findByName(playerName)?.pool ?: ArrayList()
//        val result = ArrayList<UnitDTO>(list.size)
//        for (unit in list) result.add(buildUnit(unit))
//        return result;
        val list = unitRepo.findAllByPlayer_Name(playerName)
        val result = ArrayList<UnitDTO>(list.size)
        for (unit in list) result.add(buildUnit(unit))
        return result;
    }

    fun addNewUnit(pName: String, name: String, type: Unitt.Unit.Type): UnitDTO {
        if (name.matches(Regex(regex))) {
            val id = playerRepo.getIdByName(pName)
            val player = Player(id, pName)
            val unit = buildUnitEntity(name, type, player)
            val saved = unitRepo.save(unit)
            return buildUnit(saved)
        } else throw ValidationException("Hero name doesn't match the pattern $regex")
    }

    companion object {
        const val regex = "^[A-Za-z0-9]{2,16}\$";
    }
}