package net.medrag.theBattle.model.entities

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ProfileImage(
        @Column(name = "profile_image_name")
        var name: String = "transparent",

        @Column(name = "profile_image_color")
        var color: String = "#000000",

        @Column(name = "profile_image_background")
        var background: String = "#ffffff",

        @Column(name = "profile_image_borders")
        var borders: String = "#000000")