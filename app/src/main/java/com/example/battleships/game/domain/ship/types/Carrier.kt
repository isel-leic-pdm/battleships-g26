package com.example.fleetbattletemp.game.domain.ship.types

import com.example.fleetbattletemp.game.domain.board.CoordinateSet
import com.example.fleetbattletemp.game.domain.ship.ShipType

class Carrier(override val coordinates: CoordinateSet) : Ship() {
    override val type: ShipType = ShipType.CARRIER
}
