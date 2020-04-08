package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.classes.Unitt
import net.medrag.theBattle.model.dto.Position.*
import net.medrag.theBattle.model.dto.UnitDTO
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.SquadType.FORCED_BACK
import net.medrag.theBattle.model.squad.SquadType.FORCED_FRONT
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:test.properties"])
internal class AttackServiceTest {

    @TestConfiguration
    @ComponentScan("net.medrag.theBattle")
    internal class TestConfig

    @Autowired
    lateinit var attackService: AttackService

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }


    /**
     *
     *
     *                      FORCED_BACK to FORCED_BACK attack validation
     *
     *
     */

    /**
     * B.POS1 -> B.POS4, !condition 3
     */
    @Test
    fun validateMeleeAttackFromForcedBackToForcedBackCase04() {
        val player = createMockSquad(FORCED_BACK, pos1 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS4), player, foe) }
    }

    /**
     * B.POS5 [POS4] -> B.POS4, condition 3
     */
    @Test
    fun validateMeleeAttackFromForcedBackToForcedBackCase03() {
        val player = createMockSquad(FORCED_BACK, pos5 = true, pos4 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS5, listOf(POS4), player, foe) }
        assertEquals("e011", e.message?.substring(7, 11))
    }

    /**
     * B.POS3 [POS4] -> B.POS4, condition 2
     */
    @Test
    fun validateMeleeAttackFromForcedBackToForcedBackCase02() {
        val player = createMockSquad(FORCED_BACK, pos3 = true, pos4 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS3, listOf(POS4), player, foe) }
        assertEquals("e010", e.message?.substring(7, 11))
    }

    /**
     * B.POS3 -> B.POS4, !condition 1
     */
    @Test
    fun validateMeleeAttackFromForcedBackToForcedBackCase01() {
        val player = createMockSquad(FORCED_BACK, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS3, listOf(POS4), player, foe) }
    }


    /**
     *
     *
     *                      FORCED_FRONT to FORCED_BACK attack validation
     *
     *
     */


    /**
     * F.POS2 -> B.POS4, !condition 1
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase12() {
        val player = createMockSquad(FORCED_FRONT, pos2 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS2, listOf(POS4), player, foe) }
    }

    /**
     * F.POS2 [POS3] -> B.POS4, condition 1
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase11() {
        val player = createMockSquad(FORCED_FRONT, pos2 = true, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS2, listOf(POS4), player, foe) }
        assertEquals("e004", e.message?.substring(7, 11))
    }

    /**
     * F.POS5 [POS3] -> B.[POS4] POS2, !condition 2
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase10() {
        val player = createMockSquad(FORCED_FRONT, pos5 = true, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos2 = true, pos4 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS5, listOf(POS2), player, foe) }
        assertEquals("e005", e.message?.substring(7, 11))
    }

    /**
     * F.POS1 [POS3] -> B.POS4, condition 2.4
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase09() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS4), player, foe) }
    }

    /**
     * F.POS1 [POS3] -> B.[POS4] POS2, condition 2.3
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase08() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos2 = true, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS2), player, foe) }
    }

    /**
     * F.POS3 -> B.POS4, condition 2.2
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase07() {
        val player = createMockSquad(FORCED_FRONT, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS3, listOf(POS4), player, foe) }
    }

    /**
     * F.POS1 -> B.[POS2] POS4, condition 2.1
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase06() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true)
        val foe = createMockSquad(FORCED_BACK, pos4 = true, pos2 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS4), player, foe) }
    }

    /**
     * F.POS1 [POS3] -> B.[POS2] POS5, !condition 4
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase05() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true, pos3 = true)
        val foe = createMockSquad(FORCED_BACK, pos5 = true, pos2 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS5), player, foe) }
    }

    /**
     * F.POS1 -> B.[POS4] POS3, condition 4
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase04() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true)
        val foe = createMockSquad(FORCED_BACK, pos3 = true, pos4 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS1, listOf(POS3), player, foe) }
        assertEquals("e006", e.message?.substring(7, 11))
    }

    /**
     * F.POS1 -> B.[POS2] POS1, condition 4
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase03() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true)
        val foe = createMockSquad(FORCED_BACK, pos1 = true, pos2 = true)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
        assertEquals("e006", e.message?.substring(7, 11))
    }

    /**
     * F.POS1 -> B.POS1, condition 3
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase02() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true)
        val foe = createMockSquad(FORCED_BACK, pos1 = true)
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
    }

    /**
     * Target is already dead
     */
    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase01() {
        val player = createMockSquad(FORCED_FRONT, pos1 = true)
        val foe = createMockSquad(FORCED_BACK)
        val e = assertThrows<ValidationException> { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
        assertEquals("e000", e.message?.substring(7, 11))
    }

    private fun createMockSquad(type: SquadType = FORCED_FRONT, pos1: Boolean = false, pos2: Boolean = false,
                                pos3: Boolean = false, pos4: Boolean = false, pos5: Boolean = false): ValidatedSquad {
        val unit1 = UnitDTO(1, "Fighter-1", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())
        val unit2 = UnitDTO(2, "Fighter-2", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())
        val unit3 = UnitDTO(3, "Fighter-3", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())
        val unit4 = UnitDTO(4, "Fighter-4", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())
        val unit5 = UnitDTO(5, "Fighter-5", 0, 0, 0, type = Unitt.Unit.Type.FIGHTER.getInstance())
        val squad = ValidatedSquad("noname", type, unit1, unit2, unit3, unit4, unit5)
        squad.pos1.hp = if (pos1) 1 else 0
        squad.pos2.hp = if (pos2) 1 else 0
        squad.pos3.hp = if (pos3) 1 else 0
        squad.pos4.hp = if (pos4) 1 else 0
        squad.pos5.hp = if (pos5) 1 else 0
        return squad
    }
}