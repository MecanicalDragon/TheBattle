package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
class Warrior : Unitt(){
    override var name: String = "Warrior"
    override var attack: Int = 5
    override var initiative: Int = 10
    override var health: Int = 15
    override var defence: Int = 1
    override var accuracy: Int = 8
    override var evasion: Int = 1
}