package net.medrag.theBattle.model.dto


/**
 * {@author} Stanislav Tretyakov
 * 20.01.2020
 */
data class ActionResult(val action: ActionType,
                        val nextUnit: UnitDTO,
                        val lastMoveTimestamp: Long,
                        val comments: String,
                        val additionalData: Map<String, Any> = mapOf(),
                        val finished: Boolean = false)