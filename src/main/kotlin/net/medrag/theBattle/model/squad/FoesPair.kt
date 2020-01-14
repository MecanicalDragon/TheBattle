package net.medrag.theBattle.model.squad

import net.medrag.theBattle.model.classes.ValidatedSquad
import java.util.*


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class FoesPair(
        val foe1: ValidatedSquad,
        val foe2: ValidatedSquad)