package com.example.battleships.services.models

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