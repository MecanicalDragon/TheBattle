package net.medrag.theBattle.model

import java.lang.RuntimeException


/**
 * Throw it when data valid, but incompatible with already existing data
 *
 * {@author} Stanislav Tretyakov
 * 10.02.2020
 */
class IncompatibleDataException(message: String) : RuntimeException(message)