package com.example.battleships.services.models

import com.example.battleships.game.domain.game.Configuration
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

data class ShipOutputModel(val shipType: ShipType,
                           val position : Coordinate,
                           val orientation: Orientation
                           ): OutputModel

internal class PlaceShipOutputModel(val ships : List<ShipOutputModel>) :OutputModel{
    val operation = "place-ships"
    val fleetConfirmed = "true"
}


internal data class ConfigurationOutputModel(
    val boardSize: Int,
    val fleet: Map<ShipType, Int>,
    val shots: Long,
    val roundTimeout: Long
) : OutputModel {
    companion object{
        fun transform(configuration: Configuration) =
            ConfigurationOutputModel(
                configuration.boardSize,
                configuration.fleet.toMap(),
                configuration.shots,
                configuration.roundTimeout
            )
    }
}