package net.medrag.theBattle.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.medrag.theBattle.model.GAME_FOUND
import net.medrag.theBattle.model.classes.Squad
import net.medrag.theBattle.model.squad.FoesPair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@Service
class BattleSearchingService(@Autowired private val wSocket: SimpMessagingTemplate) {

    companion object {
        private val searching = ConcurrentLinkedQueue<Squad>()
        private var battleFoes = ConcurrentHashMap<UUID, FoesPair>()

        private val searchingJob = GlobalScope.async {
            while (true) {

                var foe1: Squad? = null
                var foe2: Squad? = null

                while (foe1 == null) {
                    foe1 = searching.poll()
                    //pause coroutine
                    if (foe1 == null) delay(1000)
                }
                //TODO: need better solution
                while (foe2 == null) {
                    foe2 = searching.poll()
                    //pause coroutine
                    if (foe2 == null) delay(1000)
                }
                val uuid = UUID.randomUUID()
                val pair = FoesPair(uuid, foe1, foe2)
                battleFoes[uuid] = pair
                //trigger websocket
                wSocket.convertAndSend("/game/messages", GAME_FOUND)
            }
        }

        init {
            searchingJob.start()
        }
    }

    fun add(squad: Squad) {
        val foe = searching.poll()
        if (foe == null) {
            searching.add(squad)
            //unpause coroutine
        } else {
            val uuid = UUID.randomUUID()
            val pair = FoesPair(uuid, foe, squad)
            battleFoes[uuid] = pair
        }
    }
}