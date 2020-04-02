package net.medrag.theBattle.model


/**
 * {@author} Stanislav Tretyakov
 * 24.12.2019
 */
const val GAME_FOUND: String = "GAME_FOUND"
const val AWAIT = "AWAIT"
const val START = "START"
const val RETIRED = "RETIRED"

const val ACCURACY_MODIFIER = 3
const val INITIATIVE_BOTTOM_THRESHOLD = 5
const val DAMAGED_SQUAD = "DAMAGED_SQUAD"

const val TURN_TIME = 20_000L
const val ATTACK_VALIDATION_ERROR = "Attack from %s to %s is not valid under current circumstances!"
const val TARGET_DATA_INVALID = "Data of targets positions have been passed incorrectly."