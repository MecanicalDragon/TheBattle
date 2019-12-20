package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
class Archer : Unitt() {
    override var name: String = "Archer"
    override var attack: Int = 5
    override var initiative: Int = 13
    override var health: Int = 13
    override var defence: Int = 0
    override var accuracy: Int = 7
    override var evasion: Int = 2
}