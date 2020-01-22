package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.squad.ValidatedSquad


/**
 * {@author} Stanislav Tretyakov
 * 20.01.2020
 */
data class ActionResult(val action: ActionType,
                        val additionalData: Map<String, Any>?,
                        val nextUnit: UnitDTO,
                        val comments: String)