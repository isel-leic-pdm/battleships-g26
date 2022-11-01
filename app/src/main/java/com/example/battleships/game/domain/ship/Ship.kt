package pt.isel.daw.dawbattleshipgame.domain.ship

import com.example.battleships.game.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
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