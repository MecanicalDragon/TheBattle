package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Player
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
@Repository
interface PlayerRepo : CrudRepository<Player, Int> {
    fun findByName(name: String): Player?
}