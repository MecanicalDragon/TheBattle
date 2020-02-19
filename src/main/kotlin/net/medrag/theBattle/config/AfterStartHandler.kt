package net.medrag.theBattle.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils


/**
 * {@author} Stanislav Tretyakov
 * 18.02.2020
 */
@Component
class AfterStartHandler {

    @Autowired
    lateinit var factory: ConfigurableListableBeanFactory

    @EventListener
    fun handleAfterStart(event: ContextRefreshedEvent) {
        if (!initialized) {
            initialized = true
            val ctx = event.applicationContext
            val beanDefinitionNames = ctx.beanDefinitionNames
            for (name in beanDefinitionNames) {
                val beanDefinition = factory.getBeanDefinition(name)
                beanDefinition.beanClassName?.let {
                    val beanClass = ClassUtils.resolveClassName(it, ClassLoader.getSystemClassLoader())
                    val methods = beanClass.methods
                    for (m in methods) {
                        if (m.isAnnotationPresent(AfterStart::class.java)) {
                            val bean = ctx.getBean(name)
                            m.invoke(bean)
                        }
                    }
                }
            }
        }
    }

    companion object {
        var initialized = false
    }

}