package net.medrag.theBattle.model.dto

import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.entities.Player
import net.medrag.theBattle.model.entities.UnitEntity


/**
 * {@author} Stanislav Tretyakov
 * 25.12.2019
 */
fun buildUnit(entity: UnitEntity): UnitDTO = UnitDTO(entity.id, entity.name, entity.level, entity.expirience,
        entity.type.getInstance())

fun buildUnitEntity(name: String, type: Unitt.Unit.Type, player: Player): UnitEntity =
        UnitEntity(null, name, 1, 0, type, player)