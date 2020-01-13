package net.medrag.theBattle.service

import kotlinx.coroutines.*
import net.medrag.theBattle.model.GAME_FOUND
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Squad
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.squad.FoesPair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@Service
class BattleSearchingService(@Autowired private val wSocket: SimpMessagingTemplate) {

    private val searching = ConcurrentLinkedDeque<Squad>()
    private var battleFoes = ConcurrentHashMap<UUID, FoesPair>()

    private val searchingJob = GlobalScope.async(start = CoroutineStart.LAZY) {
        while (true) {

            val foe1: Squad? = searching.poll()
            if (foe1 == null) {
                delay(1000)
                continue
            }

            val foe2: Squad? = searching.poll()
            if (foe2 == null) {
                while (true) {
                    if (searching.offerFirst(foe1)) break
                    delay(500)
                }
                continue
            }

            val uuid = UUID.randomUUID()
            val pair = FoesPair(uuid, foe1, foe2)
            battleFoes[uuid] = pair

            //trigger websockets
            wSocket.convertAndSend("/searching/${foe1.player.name}", "$GAME_FOUND->$uuid")
            wSocket.convertAndSend("/searching/${foe2.player.name}", "$GAME_FOUND->$uuid")
        }
    }

    init {
        searchingJob.start()
    }

    fun test() {
        println("received")
        GlobalScope.launch {
            println("wait...")
            delay(5000)
            println("now!")
            wSocket.convertAndSend("/searching/asdd", "$GAME_FOUND->${UUID.randomUUID()}")
            println("sent")
        }
    }

    /**
     * Looks for a foe in 'searching'-queue.
     * Returns uuid of the battle, if foe was found, otherwise null
     */
    fun getFoeOrAddToQueue(squad: Squad): UUID? {
        searching.poll()?.let {
            val uuid = UUID.randomUUID()
            val pair = FoesPair(uuid, it, squad)
            battleFoes[uuid] = pair
            wSocket.convertAndSend("/searching/${it.player.name}", "$GAME_FOUND->$uuid")
            return uuid
        }
        searching.add(squad)
        return null
    }

    fun cancelBid(squad: Squad) = searching.remove(squad)

    fun getBattleByUuid(bud: String): FoesPair {
        battleFoes[UUID.fromString(bud)]?.let {
            return it
        }
        throw ValidationException("No battle with uuid '$bud'")
    }
}