package com.example.battleships.utils

import com.example.battleships.game.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType


fun getGameTestConfiguration1() = Configuration(
    boardSize = 13,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)

fun getGameTestConfiguration2() = Configuration(
    boardSize = 10,
    fleet = mapOf(
        ShipType.BATTLESHIP to 4,
        ShipType.DESTROYER to 2
    ),
    shots = 5,
    roundTimeout = 10
)

fun getGameTestConfiguration3() = Configuration(
    boardSize = 8,
    fleet = mapOf(
        Pair(ShipType.DESTROYER, 2)
    ),
    shots = 5,
    roundTimeout = 10
)

fun getGameTestConfiguration4() = Configuration(
    boardSize = 10,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)

fun getGameTestConfiguration5() = Configuration(
    boardSize = 13,
    shots = 5,
    fleet = mapOf(
        Pair(ShipType.CARRIER, 5),
        Pair(ShipType.BATTLESHIP, 4),
        Pair(ShipType.CRUISER, 3),
        Pair(ShipType.SUBMARINE, 3),
        Pair(ShipType.DESTROYER, 2)
    ),
    roundTimeout = 10
)