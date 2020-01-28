package net.medrag.theBattle.model.classes


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
object Sage : Unitt() {
    override var type: Unit.Type = Unit.Type.SAGE
    override var distance: Unit.Distance = Unit.Distance.ANY
    override var attack: Int = 7
    override var initiative: Int = 7
    override var health: Int = 10
    override var defence: Int = 0
    override var accuracy: Int = 11
    override var evasion: Int = 0
    override var basicReward: Int = 50
}