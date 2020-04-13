package net.medrag.theBattle.repo

import net.medrag.theBattle.model.entities.Advertisement
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * {@author} Stanislav Tretyakov
 * 12.04.2020
 */
@Repository
interface AdvertisementRepo : CrudRepository<Advertisement, Long>