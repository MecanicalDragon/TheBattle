package net.medrag.theBattle.model.classes


/**
 * @author Stanislav Tretyakov
 * 18.12.2019
 */
object Ranger : Unitt() {
    override var type: Unit.Type = Unit.Type.RANGER
    override var distance: Unit.Distance = Unit.Distance.RANGED
    override var attack: Int = 5
    override var initiative: Int = 13
    override var health: Int = 13
    override var defence: Int = 0
    override var accuracy: Int = 7
    override var evasion: Int = 2
    override var basicReward: Int = 50
}