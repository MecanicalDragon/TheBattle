package net.medrag.theBattle.controller

import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class AdviceController {
    @ExceptionHandler(value = [DataAccessException::class])
    fun handleDataAccessException(e: DataAccessException) = ResponseEntity.status(555).body(e.message)
}