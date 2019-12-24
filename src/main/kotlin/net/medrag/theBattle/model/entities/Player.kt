package net.medrag.theBattle.model.entities

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
        val id: Int?,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @ElementCollection
        val pool: List<String>?)