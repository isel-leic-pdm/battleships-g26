package com.example.battleships.game.utils
import java.time.Instant

interface Clock {
    fun now(): Instant
}

object RealClock : Clock {
    override fun now(): Instant =
            Instant.ofEpochSecond(Instant.now().epochSecond)
}