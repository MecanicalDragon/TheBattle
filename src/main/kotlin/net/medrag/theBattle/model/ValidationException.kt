package net.medrag.theBattle.model

import java.lang.RuntimeException


/**
 * Throw it in case when data is invalid somehow
 *
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
class ValidationException(message: String) : RuntimeException(message)