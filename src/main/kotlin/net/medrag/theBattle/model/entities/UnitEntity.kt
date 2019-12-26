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

        @Column(name = "expirience", nullable = false)
        var expirience: Int,

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable = false)
        var type: Unitt.Unit.Type,

        @ManyToOne
        @JoinColumn(name = "player_id", nullable = false)
        var player: Player
)

//        @Column(name = "attack", nullable = false)
//        var attack: Int,
//        @Column(name = "initiative", nullable = false)
//        var initiative: Int,
//        @Column(name = "health", nullable = false)
//        var health: Int,
//        @Column(name = "defence", nullable = false)
//        var defence: Int,
//        @Column(name = "accuracy", nullable = false)
//        var accuracy: Int,
//        @Column(name = "evasion", nullable = false)
//        var evasion: Int,
//        @Column(name = "name", nullable = false)
//        var basicReward: Int,