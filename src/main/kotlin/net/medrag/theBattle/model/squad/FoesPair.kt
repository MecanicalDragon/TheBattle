package net.medrag.theBattle.model.squad

import net.medrag.theBattle.model.classes.Squad
import java.util.*


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
data class FoesPair(
        val uuid: UUID,
        val foe1: Squad,
        val foe2: Squad)