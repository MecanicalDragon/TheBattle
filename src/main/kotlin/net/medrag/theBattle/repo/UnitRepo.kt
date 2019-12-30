package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.UnitEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * {@author} Stanislav Tretyakov
 * 26.12.2019
 */
@Repository
interface UnitRepo : CrudRepository<UnitEntity, Long> {
    fun findAllByPlayer_Name(name: String): List<UnitEntity>
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from UnitEntity u where u.id = ?1 and u.player = ?2")
    fun deleteUnit(id: Long, playerName: Player)

}