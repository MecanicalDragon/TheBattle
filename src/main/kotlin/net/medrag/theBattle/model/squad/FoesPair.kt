package net.medrag.theBattle.model.squad

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.dto.UnitDTO


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class FoesPair(
        val foe1: ValidatedSquad,
        val foe2: ValidatedSquad) {

    @JsonIgnore
    private var turnOrder = ArrayList<UnitDTO>(10)
    @JsonIgnore
    private var turn = 0
    @JsonIgnore
    private var limit = 10

    init {
        foe1.map.values.forEach { turnOrder.add(it) }
        foe2.map.values.forEach { turnOrder.add(it) }
        calculateTurnOrder()
    }

    var actionMan = turnOrder[turn]

    fun makeMove(): UnitDTO {
        turnOrder[turn++].movePerformed = true
        //TODO: fix double move
        if (turn == limit) calculateTurnOrder(true)
        else {
            while (turn < limit && turnOrder[turn].hp == 0) {
                turn++
            }
            if (turn == limit)
                calculateTurnOrder(true)
        }
        actionMan = turnOrder[turn]
        sout()
        return actionMan
    }

    private fun calculateTurnOrder(filter: Boolean = false) {
        if (filter) turnOrder = turnOrder.filter { it.hp > 0 } as ArrayList<UnitDTO>
        for (unitDTO in turnOrder) {
            unitDTO.movePerformed = false
            unitDTO.initiative = unitDTO.type.initiative + Math.random() * (unitDTO.type.initiative.toDouble() / 2)
        }
        turnOrder.sortByDescending { it.initiative }
        turn = 0
        limit = turnOrder.size
        actionMan = turnOrder[turn]
        sout()
    }

    /**
     * Recalculates the order of acting units by it's current initiative and returns unit, who's turn now
     */
    fun recalculateOrder(): UnitDTO {
        if (turn < limit - 1 && turnOrder[turn + 1].initiative > turnOrder[turn].initiative) {
            turnOrder.sortByDescending { it.initiative }
        }
        actionMan = turnOrder[turn]
        sout()
        return actionMan
    }

    private fun sout() {
        println("turn: $turn | limit: $limit")
        turnOrder.forEach { println(it) }
        println("actionMan: $actionMan")
    }
}