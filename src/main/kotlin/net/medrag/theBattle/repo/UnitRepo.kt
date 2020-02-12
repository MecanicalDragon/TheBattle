package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.UnitEntity
import net.medrag.theBattle.model.entities.UnitStatus
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
    fun deleteUnit(id: Long, player: Player)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UnitEntity u set u.experience = u.experience + ?2 where u.id = ?1")
    fun giveXP(id: Long, xp: Int)

    fun findByIdAndPlayer(id: Long, player: Player): UnitEntity?

    fun findAllByPlayer_NameAndIdIn(playerName: String, ids: Set<Long>): Set<UnitEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UnitEntity u set u.status = ?1 where u.id in ?2")
    fun setInStatus(status: UnitStatus, ids: Collection<Long>)

    fun findAllByIdIn(ids: List<Long>): List<UnitEntity>

}