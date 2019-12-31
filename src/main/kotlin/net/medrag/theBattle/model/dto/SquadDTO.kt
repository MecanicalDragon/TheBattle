package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.squad.SquadType


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class SquadDTO(val type: SquadType,
                    val pos1: UnitDTO,
                    val pos2: UnitDTO,
                    val pos3: UnitDTO,
                    val pos4: UnitDTO,
                    val pos5: UnitDTO)