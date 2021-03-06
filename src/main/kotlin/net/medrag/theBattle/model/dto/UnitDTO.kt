package net.medrag.theBattle.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import net.medrag.theBattle.model.classes.Unitt


/**
 * @author Stanislav Tretyakov
 * 25.12.2019
 */
data class UnitDTO(
        val id: Long,
        val name: String,
        val level: Int,
        val exp: Int,
        var hp: Int,
        @JsonIgnore
        var initiative: Double = 0.0,
        val effects: ArrayList<UnitEffects> = ArrayList(),
        val type: Unitt
) {
    fun isDead() = this.hp == 0
    fun isAlive() = this.hp > 0
}