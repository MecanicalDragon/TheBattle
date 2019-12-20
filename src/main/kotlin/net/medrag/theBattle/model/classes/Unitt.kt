package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
abstract class Unitt {
    abstract var name: String
    abstract var attack: Int
    abstract var initiative: Int
    abstract var health: Int
    abstract var defence: Int
    abstract var accuracy: Int
    abstract var evasion: Int

    companion object {
        enum class Types(val unit: Unitt) {
            WARRIOR(Warrior()),
            ARCHER(Archer()),
            MAGE(Mage());
        }
    }
}