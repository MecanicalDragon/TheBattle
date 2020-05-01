package net.medrag.theBattle.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor


/**
 * @author Stanislav Tretyakov
 * 13.02.2020
 */
@Configuration
class OtherConfig {
    @Bean
    //If for some reasons Spring will not do it automatically
    fun persistenceExceptionTranslationPostProcessor() = PersistenceExceptionTranslationPostProcessor()
}