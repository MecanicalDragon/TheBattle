package net.medrag.theBattle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class TheBattleApplication : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(TheBattleApplication::class.java)
    }
}

fun main(args: Array<String>) {
    runApplication<TheBattleApplication>(*args)
}
