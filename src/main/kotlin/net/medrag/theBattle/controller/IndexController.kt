package net.medrag.theBattle.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


/**
 * @author Stanislav Tretyakov
 * 09.12.2019
 */
@Controller
class IndexController{

    @GetMapping("/")
    fun index() = "index.html"

    @GetMapping("/battle")
    fun battle() = "index.html"

    @GetMapping("/manage")
    fun manage() = "index.html"

    @GetMapping("/profile")
    fun profile() = "index.html"
}