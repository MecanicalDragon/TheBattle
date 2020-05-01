package net.medrag.theBattle.model.squad

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.dto.UnitDTO
import java.util.concurrent.atomic.AtomicBoolean


/**
 * @author Stanislav Tretyakov
 * 31.12.2019
 */
data class FoesPair(
        val foe1: ValidatedSquad,
        val foe2: ValidatedSquad,
        var lastMove: Long = System.currentTimeMillis()) {

    @JsonIgnore
    var actionInProcess: AtomicBoolean = AtomicBoolean(false)

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

    /**
     * Move actionUnit marker forward
     * @return UnitDTO - unit, who's turn now
     */
    fun makeMove(): UnitDTO {
        while (++turn < limit && turnOrder[turn].isDead()) {
        }
        if (turn == limit) calculateTurnOrder(true)
        actionMan = turnOrder[turn]
        sout()
        lastMove = System.currentTimeMillis()
        return actionMan
    }

    private fun calculateTurnOrder(filter: Boolean = false) {
        if (filter) turnOrder = turnOrder.filter { it.isAlive() } as ArrayList<UnitDTO>
        for (unitDTO in turnOrder) {
            unitDTO.initiative = unitDTO.type.initiative + Math.random() * (unitDTO.type.initiative.toDouble() / 2)
        }
        turnOrder.sortByDescending { it.initiative }
        turn = 0
        limit = turnOrder.size
    }

    /**
     * Recalculates the order of acting units by it's current initiative and returns unit, who's turn now
     * Considered only for invocation by unit, who's turn
     */
    fun recalculateOrder(): UnitDTO {
        val actor = turnOrder[turn]
        actor.initiative = actor.initiative / 3 * 2
        if (turn < limit - 1) {
            if (turnOrder[turn + 1].initiative > turnOrder[turn].initiative) {
                turnOrder.sortByDescending { it.initiative }
            }
            while (turn < limit && turnOrder[turn].isDead()) {
                turn++
            }
            actionMan = turnOrder[turn]
            println("recalculating turn order...")
            sout()
            lastMove = System.currentTimeMillis()
            return actionMan
        } else return actor
    }

    private fun sout() {
        println("turn: $turn | limit: $limit")
        turnOrder.forEach { println(it) }
        println("actionMan: $actionMan")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoesPair) return false

        if (foe1 != other.foe1) return false
        if (foe2 != other.foe2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = foe1.hashCode()
        result = 31 * result + foe2.hashCode()
        return result
    }

}