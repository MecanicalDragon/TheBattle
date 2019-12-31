package net.medrag.theBattle.model.entities

import net.medrag.theBattle.model.classes.Unitt
import javax.persistence.*


/**
 * {@author} Stanislav Tretyakov
 * 25.12.2019
 */
@Entity
@Table(name = "unit")
data class UnitEntity(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long?,

        @Column(name = "name", nullable = false)
        var name: String,

        @Column(name = "level", nullable = false)
        var level: Int,

        @Column(name = "experience", nullable = false)
        var experience: Int,

        @Column(name = "health", nullable = false)
        var hp: Int,

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        var type: Unitt.Unit.Type,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "player_id", nullable = false)
        var player: Player
)