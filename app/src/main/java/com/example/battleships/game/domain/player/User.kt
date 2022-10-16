package pt.isel.daw.dawbattleshipgame.domain.player

import com.example.battleships.game.domain.player.PasswordValidationInfo

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
)