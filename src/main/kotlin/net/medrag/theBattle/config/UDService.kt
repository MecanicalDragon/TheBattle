package net.medrag.theBattle.config

import net.medrag.theBattle.repo.PlayerRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException


/**
 * @author Stanislav Tretyakov
 * 25.12.2019
 */
@Deprecated(message="currently I don't need it")
class UDService : UserDetailsService {

    @Autowired
    lateinit var playerRepo: PlayerRepo

    override fun loadUserByUsername(playerName: String?): UserDetails {
        if (playerName != null) {
            val player = playerRepo.findByName(playerName)
                    ?: throw UsernameNotFoundException("Player $playerName does not exist.")
            return PlayerPrincipal(player.name, player.password)
        } else throw UsernameNotFoundException("Null as a $playerName parameter.")
    }
}