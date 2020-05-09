package net.medrag.theBattle.controller

import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Handles DataAccessException.
 * @author Stanislav Tretyakov
 * 12.02.2020
 */
@ControllerAdvice
class AdviceController {
    @ExceptionHandler(value = [DataAccessException::class])
    fun handleDataAccessException(e: DataAccessException): ResponseEntity<String> {
        logger.error("Exception occurred: ${e.message}. HTTP status 555 will be returned.")
        return ResponseEntity.status(555).body(e.message)
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}