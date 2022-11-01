package com.example.battleships.game.domain.state

import com.example.battleships.game.domain.ship.ShipType

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val nShotsPerRound: Int,
    val roundTimeout: Int
) {
    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null

    fun getShipLength(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType }?.second
}