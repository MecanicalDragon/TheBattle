package net.medrag.theBattle.service.api

import net.medrag.theBattle.model.ProcessingException
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.BattleBidResponse
import net.medrag.theBattle.model.dto.BattlePageResponse
import net.medrag.theBattle.model.dto.SquadDTO
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.squad.FoesPair
import net.medrag.theBattle.model.squad.ValidatedSquad
import java.util.*


/**
 * Battle arrangement-related operations.
 * @author Stanislav Tretyakov
 * 06.05.2020
 */
interface BattleServiceApi {

    /**
     * Receives battle bid from the player with name 'playerName'.
     * Function validates if squadDTO contains only player's ready for battle heroes and throws ValidationException if not.
     * Then function tries to obtain another ready for battle squad from the queue. If it exists there, function creates
     * 'FoesPair' with battle id of extracted bid, notifies foe with websockets and returns battle id with status START.
     * Otherwise new bid will be added to the searching queue with new battle uuid, it will be returned with status AWAIT.
     * @param playerName String - who's bid is it
     * @param squadDTO SquadDTO - suggested squad for the battle.
     * @return BattleBidResponse - AWAIT or START
     * @throws ValidationException if squad or player data is incorrect
     */
    @Throws(ValidationException::class)
    fun registerBattleBid(playerName: String, squadDTO: SquadDTO): BattleBidResponse

    /**
     * Finish the battle
     * @param uuid UUID - battle uuid
     * @param winner ValidatedSquad
     * @param looser ValidatedSquad
     * @param actionUnit UnitDTO - the one, who made final attack
     * @param conceded Boolean - true if battle conceded
     */
    fun finishTheBattle(uuid: UUID, winner: ValidatedSquad, looser: ValidatedSquad, actionUnit: UnitDTO, conceded: Boolean = false)

    /**
     * Share exp among units
     * @param winner ValidatedSquad
     * @param looser ValidatedSquad
     * @param finalAttack UnitDTO - who made final attack
     * @param conceded Boolean - true if battle conceded
     */
    fun giveExperience(winner: ValidatedSquad, looser: ValidatedSquad, finalAttack: UnitDTO, conceded: Boolean = false)

    /**
     * Removes squad from the searching queue.
     * @param playerName String
     * @throws ProcessingException to prevent collision if Validated squad has been removed already from searching queue
     */
    @Throws(ProcessingException::class)
    fun cancelBid(playerId: Long, playerName: String)

    /**
     * Returns player's battle uuid
     * @param playerName String
     */
    fun getBud(playerName: String): String?

    /**
     * Returns foesPair and players' profile images to render on battle page. Meant to be invoked on battle page landing.
     * @param playerName String
     * @param bud UUID
     * @return BattlePageResponse
     * @throws ValidationException if {@link #getDislocations()} throws
     */
    @Throws(ValidationException::class)
    fun getDislocationsOnBattleStart(playerName: String, bud: UUID): BattlePageResponse

    /**
     * Returns foesPair, based on Battle UUID
     * @param playerName String
     * @param bud UUID
     * @return FoesPair
     * @throws ValidationException if bud is invalid or if there is no playerName in FoesPair
     */
    @Throws(ValidationException::class)
    fun getDislocations(playerName: String, bud: UUID): FoesPair
}