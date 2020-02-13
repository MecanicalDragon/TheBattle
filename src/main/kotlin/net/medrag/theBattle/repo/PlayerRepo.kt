package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.PlayerStatus
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
@Repository
interface PlayerRepo : CrudRepository<Player, Long> {
    fun findByName(name: String): Player?

    @Query("select p.id from Player p where p.name = ?1")
    fun getIdByName(name: String): Long?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Player p set p.games = p.games + 1, p.status = ?2 where p.name = ?1")
    fun incrementGamesCount(name: String, status: PlayerStatus = PlayerStatus.FREE)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Player p set p.games = p.games + 1, p.wins = p.wins + 1, p.status = ?2 where p.name = ?1")
    fun incrementWinsAndGamesCount(name: String, status: PlayerStatus = PlayerStatus.FREE)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Player u set u.status = ?1, u.bud = ?2 where u.name in ?3")
    fun setStatusAndUUID(status: PlayerStatus, bud: String?, names: Collection<String>)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Player p set p.bud = ?2 where p.name = ?1")
    fun setBud(name: String, bud: String)

    @Query("select p.bud from Player p where p.name = ?1")
    fun getBud(name: String): String?
}