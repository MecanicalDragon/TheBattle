package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.DEFAULT_AD_URL

/**
 * Instance of this class is returned on Index page loading
 * @author Stanislav Tretyakov
 * 26.04.2020
 */
class IndexPageResponse(
        val player: PlayerDTO,
        val newsUrl: String = DEFAULT_AD_URL
)