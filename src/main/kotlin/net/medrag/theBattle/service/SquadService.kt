package net.medrag.theBattle.service

import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.squad.Squad
import net.medrag.theBattle.model.squad.SquadType
import org.springframework.stereotype.Service


/**
 * {@author} Stanislav Tretyakov
 * 19.12.2019
 */
@Service
class SquadService {

    /**
     * Get random squad
     */
    fun getSquad(): Squad {
        val squad = Squad();
        squad.type = SquadType.values()[(Math.random() * SquadType.values().size).toInt()]
        squad.pos1 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        squad.pos2 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        squad.pos3 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        squad.pos4 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        squad.pos5 = Unitt.Companion.Types.values()[(Math.random() * Unitt.Companion.Types.values().size).toInt()].unit
        return squad
    }
}