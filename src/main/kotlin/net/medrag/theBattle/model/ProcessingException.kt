package net.medrag.theBattle.model


/**
 * Throw it if data processing can not be continued because of any processing constraints
 *
 * @author Stanislav Tretyakov
 * 27.01.2020
 */
class ProcessingException(override val message: String) : RuntimeException(message) {
}