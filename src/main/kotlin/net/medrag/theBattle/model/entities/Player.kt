package net.medrag.theBattle.model.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


/**
 * {@author} Stanislav Tretyakov
 * 23.12.2019
 */
@Entity
@Table(name = "player")
data class Player(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @Column(name = "games", nullable = false)
        var games: Int = 0,

        @Column(name = "wins", nullable = false)
        var wins: Int = 0,

        @JsonIgnore
        @OneToMany(mappedBy = "player")
        var pool: List<UnitEntity> = ArrayList())