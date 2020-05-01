package net.medrag.theBattle.model.dto


/**
 * Instance of this class is returned on Manage page loading
 * @author Stanislav Tretyakov
 * 07.02.2020
 */
class ManagePageResponse(
        val player: PlayerDTO,
        val pool: List<UnitDTO> = emptyList()
)