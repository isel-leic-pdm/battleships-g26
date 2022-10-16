package com.example.battleships.game.domain.ship

import com.example.battleships.game.domain.board.Coordinate

typealias ShipSet = Set<Ship>

fun ShipSet.getShipByType(shipType: ShipType) = this.firstOrNull { it.type == shipType }

fun ShipSet.getShip(position: Coordinate) =
    this.first { it.coordinates.any { c -> c == position}}

fun ShipSet.addOrReplaceShip(ship : Ship): ShipSet {
    val auxMutableSet = this.toMutableSet()
    val entry = auxMutableSet.getShipByType(ship.type)
    if(entry != null) auxMutableSet.remove(entry)
    auxMutableSet.add(ship)
    return auxMutableSet
}

fun ShipSet.hasShip(shipType: ShipType) = this.any { it.type == shipType }
