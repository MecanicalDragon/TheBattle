package net.medrag.theBattle.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


/**
 * {@author} Stanislav Tretyakov
 * 25.12.2019
 */
data class PlayerPrincipal(
        val playerName: String,
        val pWord: String,
        val auth: MutableCollection<out GrantedAuthority> = role) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = auth
    override fun getUsername(): String = playerName
    override fun getPassword(): String = pWord
    override fun isEnabled(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true

    companion object {
        val role: MutableCollection<out GrantedAuthority> = mutableListOf(GrantedAuthority { "ROLE_PLAYER" })
    }
}