package net.medrag.theBattle.service

import net.medrag.theBattle.model.classes.Squad
import net.medrag.theBattle.model.squad.FoesPair
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean


/**
 * {@author} Stanislav Tretyakov
 * 31.12.2019
 */
@Service
class BattleSearchingService {

    companion object {
        private val searching = ConcurrentLinkedQueue<Squad>()
        private var searchingProcess: AtomicBoolean = AtomicBoolean(false)  // delete
        private var battleFoes = ConcurrentHashMap<UUID, FoesPair>()

        private fun lookingForFoes() {
            while(searchingProcess.get()){

                var foe1: Squad? = null
                var foe2: Squad? = null

                while(foe1 == null){
                    //pause coroutine
                    foe1 = searching.poll()
                }
                while(foe2 == null){
                    //pause coroutine
                    foe2 = searching.poll()
                }
                val uuid = UUID.randomUUID()
                val pair = FoesPair(uuid, foe1, foe2)
                battleFoes[uuid] = pair
                //trigger websocket
            }
        }

        init {
            lookingForFoes()
        }
    }


    fun add(squad: Squad) {
        searching.add(squad)
        //unpause coroutine
        if (!searchingProcess.get() && searching.size > 1) {
            searchingProcess.compareAndSet(false, true)
            lookingForFoes()
        }
    }

    //remove method
    fun poll(): Squad {
        val squad = searching.poll()
        if (searching.size < 2 && searchingProcess.get()) {
            searchingProcess.compareAndSet(true, false)
        }
        return squad
    }
}