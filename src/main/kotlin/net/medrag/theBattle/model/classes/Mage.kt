package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
class Mage : Unitt() {
    override var name: String = "Mage"
    override var attack: Int = 8
    override var initiative: Int = 7
    override var health: Int = 10
    override var defence: Int = 0
    override var accuracy: Int = 10
    override var evasion: Int = 0
}