package net.medrag.theBattle.model

import java.lang.RuntimeException


/**
 * {@author} Stanislav Tretyakov
 * 24.01.2020
 */
class VictoryException(val winner: String) : RuntimeException()