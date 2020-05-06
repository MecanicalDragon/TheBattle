package net.medrag.theBattle.controller

import net.medrag.theBattle.model.ValidationException
import net.medrag.theBattle.model.entities.ProfileImage
import net.medrag.theBattle.model.PlayerSession
import net.medrag.theBattle.service.api.PlayerServiceApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


/**
 * @author Stanislav Tretyakov
 * 30.04.2020
 */
@RestController
@RequestMapping("/profile")
class ProfileController(@Autowired private val session: PlayerSession,
                        @Autowired private val playerService: PlayerServiceApi) {

    /**
     * Save new profile image for the player
     * @param ava ProfileImage - new profile image
     * @return ResponseEntity<Void>:
     *      - 200 if request was successful
     *      - 400 if not
     *      - 401 if unauthorized
     *      - 555 if database problem occurs
     */
    @PostMapping("/save")
    fun save(@RequestBody ava: ProfileImage): ResponseEntity<Void> {
        try {
            session.playerName?.let {
                val id: Long = session.playerId ?: -1L
                playerService.saveNewProfileImage(id, ava)
                return ResponseEntity.ok().build()
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: ValidationException) {
            return ResponseEntity.badRequest().build()
        }
    }

    /**
     * Retrieve a page of avatar images
     * @param page Int - page number
     * @return ResponseEntity<Any>:
     *      - 200 if success
     *      - 401 if unauthorized
     *      - 555 if db error occurred
     */
    @GetMapping("/avatars")
    fun getAvatars(page: Int = 0): ResponseEntity<Any> {
        session.playerName?.let {
            return ResponseEntity.ok(playerService.getAvatarsPage(page))
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}