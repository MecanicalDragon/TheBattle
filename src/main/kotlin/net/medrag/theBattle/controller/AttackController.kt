package net.medrag.theBattle.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


/**
 * {@author} Stanislav Tretyakov
 * 15.01.2020
 */
@RestController
@RequestMapping("/attack")
class AttackController(){
    @PostMapping("/")
    fun attack(){

    }
}