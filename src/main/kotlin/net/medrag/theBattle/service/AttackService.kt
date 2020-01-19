package net.medrag.theBattle.service

import net.medrag.theBattle.model.ACCURACY_MODIFIER
import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.ValidatedSquad
import net.medrag.theBattle.model.dto.AttackAction
import net.medrag.theBattle.model.dto.UnitDTO
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttackService(private val battleService: BattleService) {
    fun performAttack(playerName: String, bud: UUID, attackAction: AttackAction): String {
        val pair = battleService.getDislocations(playerName, bud)
        val attackersSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe1 else pair.foe2
        val sufferingSquad: ValidatedSquad = if (pair.foe1.playerName == playerName) pair.foe2 else pair.foe1
        //TODO: validate attack
        //TODO: calculate damage
        val attackingUnit: UnitDTO = mapPosition(attackAction.attacker, attackersSquad)
        val targets: List<UnitDTO> = attackAction.targets.map { mapPosition(it, sufferingSquad) }
        calculateHitChance(targets, attackingUnit)

        //TODO: return not string, but calculated damage
        return "ok"
    }

    private fun calculateHitChance(targets: List<UnitDTO>, attacker: UnitDTO) {
        val accuracy = attacker.type.accuracy + ACCURACY_MODIFIER
        for (it in targets){
            if (Math.random() * accuracy > it.type.evasion) {
                val damage = attacker.type.attack - it.type.defence
                var hp = attacker.type.health - damage
                if (hp < 0) hp = 0
                val suffered = it.copy(hp = hp)
            }
        }


    }

    private fun mapPosition(position: String, squad: ValidatedSquad): UnitDTO = when (position) {
        "pos1" -> squad.pos1
        "pos2" -> squad.pos2
        "pos3" -> squad.pos3
        "pos4" -> squad.pos4
        "pos5" -> squad.pos5
        else -> throw ValidationException("wtf? Unit position cannot be $position")
    }
}