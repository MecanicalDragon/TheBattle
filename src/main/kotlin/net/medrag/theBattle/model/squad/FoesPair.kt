package net.medrag.theBattle.model.squad

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.classes.ValidatedSquad
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
    }
}