package net.medrag.theBattle.service

import net.medrag.theBattle.model.DEFAULT_AD_URL
import net.medrag.theBattle.repo.AdvertisementRepo
import net.medrag.theBattle.service.api.AdServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


/**
 * @author Stanislav Tretyakov
 * 12.04.2020
 */
@Service
class AdService(@Autowired private val adRepo: AdvertisementRepo) : AdServiceApi{

    private lateinit var links: List<String>

    override fun getRandomLink(): String = links[(Math.random() * links.size).toInt()]

    @PostConstruct
    private fun init() {
        links = adRepo.findAll().map { it.link }.ifEmpty { listOf(DEFAULT_AD_URL) }
    }
}