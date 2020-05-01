package net.medrag.theBattle.model.entities

import javax.persistence.*


/**
 * @author Stanislav Tretyakov
 * 30.04.2020
 */
@Entity
@Table(name = "avatar_image")
data class Avatar(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Column(name = "image", nullable = false, unique = true)
        val image: String
)