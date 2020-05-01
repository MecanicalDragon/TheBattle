package net.medrag.theBattle.model.dto


/**
 * Instance of this class is returned on any player's action in battle.
 * @author Stanislav Tretyakov
 * 20.01.2020
 */
class ActionResult(val action: ActionType,
                        val nextUnit: UnitDTO,
                        val lastMoveTimestamp: Long,
                        val comments: String,
                        val additionalData: Map<String, Any> = mapOf(),
                        val finished: Boolean = false)