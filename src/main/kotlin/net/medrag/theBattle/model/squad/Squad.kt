package net.medrag.theBattle.model.squad

import net.medrag.theBattle.model.classes.Unitt


/**
 * {@author} Stanislav Tretyakov
 * 18.12.2019
 */
class Squad {
    var type: SquadType = SquadType.FORCED_FRONT
    var pos1: Unitt? = null
    var pos2: Unitt? = null
    var pos3: Unitt? = null
    var pos4: Unitt? = null
    var pos5: Unitt? = null
}