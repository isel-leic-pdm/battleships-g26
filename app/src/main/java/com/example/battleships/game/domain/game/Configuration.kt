package com.example.battleships.game.domain.game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Map<ShipType, Int>, // List<Ship:Occupation>
    val shots: Int,
    val roundTimeout: Long
) {
    fun isShipValid(shipType: ShipType) = fleet[shipType]  != null

    fun getShipLength(shipType: ShipType) = fleet[shipType]
}