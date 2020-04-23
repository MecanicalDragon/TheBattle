package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.DEFAULT_AD_URL


/**
 * {@author} Stanislav Tretyakov
 * 07.02.2020
 */
class ManagePageResponse(
        val player: PlayerDTO,
        val pool: List<UnitDTO> = emptyList(),
        val newsUrl: String = DEFAULT_AD_URL)