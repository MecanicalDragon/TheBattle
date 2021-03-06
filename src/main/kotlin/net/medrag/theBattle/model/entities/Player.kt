package net.medrag.theBattle.model.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


/**
 * @author Stanislav Tretyakov
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

        @Embedded
        var profileImage: ProfileImage = ProfileImage(),

        @Column(name = "games", nullable = false)
        var games: Int = 0,

        @Column(name = "wins", nullable = false)
        var wins: Int = 0,

        @Column(name = "password", nullable = false)
        var password: String = "",

        @Column(name = "status", nullable = false)
        @Enumerated(value = EnumType.STRING)
        var status: PlayerStatus = PlayerStatus.FREE,

        @Column(name = "bud", nullable = true, unique = false)
        val bud: String? = null,

        @JsonIgnore
        @OneToMany(mappedBy = "player")
        var pool: List<UnitEntity> = ArrayList()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}