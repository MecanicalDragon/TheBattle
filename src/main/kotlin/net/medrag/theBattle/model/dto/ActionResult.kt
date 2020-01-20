package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.classes.ValidatedSquad


/**
 * {@author} Stanislav Tretyakov
 * 20.01.2020
 */
data class ActionResult(val actionPerformed: String,
                        val sufferedSquad: ValidatedSquad,
                        val nextUnit: UnitDTO)