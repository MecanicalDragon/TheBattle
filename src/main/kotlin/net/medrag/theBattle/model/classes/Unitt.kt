package net.medrag.theBattle.model.classes

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classType")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = Fighter::class, name = "Fighter"),
    JsonSubTypes.Type(value = Ranger::class, name = "Ranger"),
    JsonSubTypes.Type(value = Sage::class, name = "Sage")
])
abstract class Unitt {
    abstract var type: Type
    abstract var attack: Int
    abstract var initiative: Int
    abstract var health: Int
    abstract var defence: Int
    abstract var accuracy: Int
    abstract var evasion: Int
    abstract var basicReward: Int
    abstract var distance: Distance

    companion object Unit {

        enum class Type(val className: String) {
            FIGHTER("Fighter"),
            RANGER("Ranger"),
            SAGE("Sage");

            fun getInstance(): Unitt {
                return when (this.className) {
                    "Fighter" -> Fighter()
                    "Ranger" -> Ranger()
                    "Sage" -> Sage()
                    else -> Sage()
                }
            }
        }

        enum class Distance {
            CLOSED,
            RANGED
        }
    }
}