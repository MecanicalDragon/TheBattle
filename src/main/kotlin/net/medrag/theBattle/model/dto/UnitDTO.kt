package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.classes.Unitt


/**
 * {@author} Stanislav Tretyakov
 * 25.12.2019
 */
data class UnitDTO(
        val id: Long,
        val name: String,
        val level: Int,
        val exp: Int,
        var hp: Int,
        var initiative: Double = 0.0,
        var movePerformed: Boolean = false,
        val type: Unitt
)