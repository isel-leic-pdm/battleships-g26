package com.example.battleships.game.domain.player

enum class Player {
    ONE, TWO;
    fun other() = if (this == ONE) TWO else ONE
}