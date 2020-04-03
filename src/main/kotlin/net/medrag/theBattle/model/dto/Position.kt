package net.medrag.theBattle.model.dto


/**
 * {@author} Stanislav Tretyakov
 * 20.01.2020
 */
enum class Position(private val strongLine: Boolean) {
    POS1(true),
    POS2(false),
    POS3(true),
    POS4(false),
    POS5(true);

    fun isStrongLine() = this.strongLine
    fun isWeakLine() = !this.strongLine
}