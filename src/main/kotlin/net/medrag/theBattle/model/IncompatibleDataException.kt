package net.medrag.theBattle.model

import java.lang.RuntimeException


/**
 * {@author} Stanislav Tretyakov
 * 10.02.2020
 * Thrown when data valid, but incompatible with already existing data
 */
class IncompatibleDataException(message: String) : RuntimeException(message)