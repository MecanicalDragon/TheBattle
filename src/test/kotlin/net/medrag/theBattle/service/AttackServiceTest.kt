package net.medrag.theBattle.service

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.dto.Position
import net.medrag.theBattle.model.dto.Position.POS1
import net.medrag.theBattle.model.dto.Position.POS2
import net.medrag.theBattle.model.squad.SquadType
import net.medrag.theBattle.model.squad.SquadType.FORCED_BACK
import net.medrag.theBattle.model.squad.SquadType.FORCED_FRONT
import net.medrag.theBattle.model.squad.ValidatedSquad
import org.junit.jupiter.api.*
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

    private lateinit var actor: Position
    private lateinit var targets: List<Position>
    private lateinit var player: ValidatedSquad
    private lateinit var foe: ValidatedSquad

    @BeforeEach
    fun setUp() {
        actor = POS1
        targets = listOf(POS2)
        player = ValidatedSquad("player")
        foe = ValidatedSquad("foe")
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase03() {
        val foe = createMockSquad(FORCED_BACK, pos1 = true, pos2 = true)
        assertThrows<ValidationException> { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
    }

    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase02() {
        foe.pos1.hp = 1
        assertDoesNotThrow { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
    }

    @Test
    fun validateMeleeAttackFromForcedFrontToForcedBackCase01() {
        assertThrows<ValidationException> { attackService.validateMeleeAttack(POS1, listOf(POS1), player, foe) }
    }

    private fun createMockSquad(type: SquadType = FORCED_FRONT, pos1: Boolean = false, pos2: Boolean = false,
                                pos3: Boolean = false, pos4: Boolean = false, pos5: Boolean = false): ValidatedSquad {
        val squad = ValidatedSquad("noname", type)
        squad.pos1.hp = if (pos1) 1 else 0
        squad.pos2.hp = if (pos2) 1 else 0
        squad.pos3.hp = if (pos3) 1 else 0
        squad.pos4.hp = if (pos4) 1 else 0
        squad.pos5.hp = if (pos5) 1 else 0
        return squad
    }
}