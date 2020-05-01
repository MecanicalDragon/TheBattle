package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Avatar
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * @author Stanislav Tretyakov
 * 30.04.2020
 */
@Repository
interface AvatarRepo : CrudRepository<Avatar, Long> {
    fun findByImage(name: String): Avatar?
    fun findAll(pageable: Pageable): Page<Avatar>
}
