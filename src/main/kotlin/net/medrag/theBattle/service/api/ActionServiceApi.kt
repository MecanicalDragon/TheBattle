package net.medrag.theBattle.service.api

import net.medrag.theBattle.model.ProcessingException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.ActionResult
import net.medrag.theBattle.model.dto.SimpleAction
import java.util.*


/**
 * @author Stanislav Tretyakov
 * 06.05.2020
 */
interface ActionServiceApi {

    /**
     * SimpleAction handling
     * @param playerName String - action performer
     * @param bud UUID - battle UUID
     * @param simpleAction SimpleAction - action itself
     * @return ActionResult - result data object
     * @throws ValidationException if:
     *          - action unit is dead
     *          - other unit's turn
     *          - attack is invalid
     *          - position is passed incorrectly
     *          - position data is invalid
     * @throws ProcessingException if another player already acts.
     */
    @Throws(ValidationException::class, ProcessingException::class)
    fun performSimpleAction(playerName: String, bud: UUID, simpleAction: SimpleAction): ActionResult

    /**
     * Shifts turn to next unit if turn time exceeded.
     * If it's possible, applies to current unit {@link ActionType.WAIT}. If not - just passes.
     * Result is passed by websocket.
     * @param playerName String
     * @param bud UUID
     */
    fun pingTurn(playerName: String, bud: UUID)

    /**
     * Send message to the during battle.
     * @param playerName String - who sends
     * @param bud UUID
     * @param msgNumber Int - number of message
     * @throws ValidationException if battle is over already.
     */
    @Throws(ValidationException::class)
    fun sendMessageToTheFoe(playerName: String, bud: UUID, msgNumber: Int)
}