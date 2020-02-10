package net.medrag.theBattle.model

import java.lang.RuntimeException


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 * Thrown in any invalid data case
 */
class ValidationException(message: String) : RuntimeException(message)