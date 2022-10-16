package com.example.fleetbattletemp.game.domain.player

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
)