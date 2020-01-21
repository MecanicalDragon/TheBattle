package net.medrag.theBattle.model.squad

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.UnitDTO


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class FoesPair(
        val foe1: ValidatedSquad,
        val foe2: ValidatedSquad) {

    @JsonIgnore
    private val turnOrder = Array(10) {
        if (it < 5) {
            foe1.map[Position.values()[it]] as UnitDTO
        } else {
            foe2.map[Position.values()[it - 5]] as UnitDTO
        }
    }

    @JsonIgnore
    private var turn = 0
    var actionMan = turnOrder[turn]

    init {
        calculateTurnOrder()
    }

    fun makeMove(): UnitDTO {
        turnOrder[turn++].movePerformed = true
        if (turn == 10) calculateTurnOrder()
        actionMan = turnOrder[turn]
        sout()
        return actionMan
    }

    private fun calculateTurnOrder() {
        for (unitDTO in turnOrder) {
            unitDTO.movePerformed = false
            unitDTO.initiative = unitDTO.type.initiative + Math.random() * (unitDTO.type.initiative.toDouble() / 2)
        }
        turnOrder.sortByDescending { it.initiative }
        turn = 0
        actionMan = turnOrder[turn]
        sout()
    }

    /**
     * Recalculates the order of acting units by it's current initiative and returns unit, who's turn now
     */
    fun recalculateOrder(): UnitDTO {
        if (turn != 9 && turnOrder[turn + 1].initiative > turnOrder[turn].initiative) {
            turnOrder.sortByDescending { it.initiative }
        }
        sout()
        actionMan = turnOrder[turn]
        return actionMan
    }

    private fun sout(){
        println("turn:")
        println(turn)
        turnOrder.forEach {
            println(it)
        }
        println("actionMan:")
        println(actionMan)
    }
}