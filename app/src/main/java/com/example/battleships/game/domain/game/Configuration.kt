package com.example.battleships.game.domain.game
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * Represents the game configuration
 */
data class Configuration(
    val boardSize: Int,
    val fleet: Map<ShipType, Int>, // List<Ship:Occupation>
    val shots: Long,
    val roundTimeout: Long
) {
    fun isShipValid(shipType: ShipType) = fleet[shipType]  != null

    fun getShipLength(shipType: ShipType) = fleet[shipType]
    companion object{
        val DEFAULT = Configuration(
            boardSize = 10,
            shots = 1,
            fleet = mapOf(
                ShipType.CARRIER to 5,
                ShipType.BATTLESHIP to 4,
                ShipType.CRUISER to 3,
                ShipType.SUBMARINE to 3,
                ShipType.DESTROYER to 2,
            ),
            roundTimeout = 120
        )
    }
}