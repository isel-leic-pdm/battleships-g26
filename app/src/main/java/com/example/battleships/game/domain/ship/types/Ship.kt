package com.example.fleetbattletemp.game.domain.ship.types

import com.example.fleetbattletemp.game.domain.board.CoordinateSet
import com.example.fleetbattletemp.game.domain.ship.Orientation
import com.example.fleetbattletemp.game.domain.ship.ShipType

sealed class Ship {
    abstract val coordinates: CoordinateSet
    abstract val type: ShipType
}

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