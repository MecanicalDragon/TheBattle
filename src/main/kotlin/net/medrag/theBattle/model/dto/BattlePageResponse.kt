package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.entities.ProfileImage
import net.medrag.theBattle.model.squad.FoesPair


/**
 * Instance of this class is returned on battle page loading
 * {@author} Stanislav Tretyakov
 * 25.04.2020
 */
class BattlePageResponse(val dislocations:FoesPair, val playersAvatar: ProfileImage, val foesAvatar: ProfileImage)