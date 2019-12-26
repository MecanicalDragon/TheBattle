package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.UnitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * {@author} Stanislav Tretyakov
 * 26.12.2019
 */
@Repository
interface UnitRepo : CrudRepository<UnitEntity, Long> {
    fun findAllByPlayer_Name(name:String): List<UnitEntity>
}