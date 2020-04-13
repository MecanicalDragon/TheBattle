package net.medrag.theBattle.model.entities

import javax.persistence.*


/**
 * {@author} Stanislav Tretyakov
 * 12.04.2020
 */
@Entity
@Table(name = "advertisement")
data class Advertisement(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long?,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @Column(name = "customer", nullable = false, unique = false)
        val customer: String,

        @Column(name = "link", nullable = false, unique = true)
        val link: String,

        @Column(name = "rate")
        val rate: Float = 1f
)