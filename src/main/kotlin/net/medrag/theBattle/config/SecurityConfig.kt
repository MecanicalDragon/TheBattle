package net.medrag.theBattle.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * @author Stanislav Tretyakov
 * 09.12.2019
 */
@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    private val swagger = arrayOf("/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**")
    private val welcomePage = arrayOf("/", "/auth/**")
    private val resources = arrayOf("/", "/**/*.woff", "/**/*.ttf", "/favicon.ico", "/**/*.png", "/**/*.gif",
            "/**/*.svg", "/**/*.jpg", "/**/*.html", "/**/*.css", "/**/*.js")

//    @Throws(Exception::class)
//    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
//        authenticationManagerBuilder
//                .userDetailsService<UserDetailsService>(udService)
//                .passwordEncoder(passwordEncoder())
//    }
//
//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Throws(Exception::class)
//    override fun authenticationManagerBean(): AuthenticationManager? {
//        return super.authenticationManagerBean()
//    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS).permitAll()
//                .antMatchers(*resources).permitAll()
//                .antMatchers(*welcomePage).permitAll()
//                .antMatchers("/**").permitAll()
//                .anyRequest().authenticated()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().applyPermitDefaultValues()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}