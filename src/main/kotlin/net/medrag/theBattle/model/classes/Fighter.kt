package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
class Fighter : Unitt(){
    override var type: Unit.Type = Unit.Type.FIGHTER
    override var distance: Unit.Distance = Unit.Distance.CLOSED
    override var attack: Int = 5
    override var initiative: Int = 10
    override var health: Int = 15
    override var defence: Int = 1
    override var accuracy: Int = 8
    override var evasion: Int = 1
    override var basicReward: Int = 50
}