package net.medrag.theBattle.filter

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession


/**
 * {@author} Stanislav Tretyakov
 * 07.02.2020
 */
//@Component
class SessionIdFilter : OncePerRequestFilter() {

    private val map = ConcurrentHashMap<String, String>()

    override fun doFilterInternal(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) {

        println("")
        println("==============")
        println(req.getSession(false)?.id)
        println(req.requestURI)
        println(req.requestURL)
        println(req.requestedSessionId)

        val headerNames = req.headerNames
        while (headerNames.hasMoreElements()) {
            val header = headerNames.nextElement()
            println("$header :: ${req.getHeader(header)}")
        }

        chain.doFilter(req, resp)

    }
}