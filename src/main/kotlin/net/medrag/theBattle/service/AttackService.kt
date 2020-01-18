package net.medrag.theBattle.service

import net.medrag.theBattle.model.dto.AttackAction
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttackService(private val battleService: BattleService) {
    fun performAttack(playerName: String, bud: UUID, attackAction: AttackAction): String {
        val pair = battleService.getDislocations(playerName, bud)
        val attacker = if(pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val damaged = if(pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        //TODO: validate attack
        //TODO: calculate damage
        //TODO: return not string, but calculated damage
        return "ok"
    }
}