package com.example.battleships.game.domain.ship

import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

data class Ship(val coordinates : CoordinateSet, val type : ShipType, val isSunk : Boolean)

fun Ship.getOrientation(): Orientation {
    if (coordinates.isEmpty()) {
        throw IllegalArgumentException("Ship must have at least one coordinate")
    }
    return if (coordinates.size == 1) {
        Orientation.HORIZONTAL
    } else {
        val first = coordinates.first()
        val second = coordinates.elementAt(1)
        if (first.column != second.column) {
            Orientation.HORIZONTAL
        } else {
            Orientation.VERTICAL
        }
    }
}

typealias ShipSet = Set<Ship>
fun ShipSet.getShip(position: Coordinate) =
    this.first { it.coordinates.any { c -> c == position}}