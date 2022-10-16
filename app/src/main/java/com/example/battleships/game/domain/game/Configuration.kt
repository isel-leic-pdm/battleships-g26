package com.example.fleetbattletemp.game.domain.game

import com.example.fleetbattletemp.game.domain.ship.ShipType

/**
 * Represents the game configuration
 */
class Configuration(
    val boardSize: Int,
    val fleet: Set<Pair<ShipType, Int>>, // List<Ship:Occupation>
    val nShotsPerRound: Int,
    val roundTimeout: Int
) {
    fun isShipValid(shipType: ShipType) =
        fleet.firstOrNull { it.first == shipType } != null

    /**
     * Retrieves the ship length according to class game configuration.
     */
    fun getShipLength(shipType: ShipType) =
        fleet.first { it.first === shipType }.second
}