package com.example.battleships.game.domain.board

import com.example.battleships.game.domain.ship.Ship
import com.example.battleships.game.domain.ship.ShipType
import kotlin.math.sqrt

class Board {
    val dimension : Int
    val board : List<Panel>

    /**
     * Initiates the board with empty panels (Water Panels)
     */
    constructor(dim: Int) {
        dimension = dim
        board = Coordinates(dim).values().map { Panel(it) }
    }

    constructor(board: List<Panel>) {
        dimension = sqrt(board.size.toDouble()).toInt()
        this.board = board
    }

    operator fun List<Panel>.get(c: Coordinate): Panel {
        return board[getIdx(c)]
    }

    private fun getIdx(c: Coordinate) = c.checkValid(dimension).let {
        (c.row - 1) * dimension + (c.column - 1)
    }

    override fun toString(): String {
        var str = "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n"
        for (i in 0 until dimension) {
            str += "| ${i+1}"
            str = if (i+1 < 10 ) "$str |" else "$str|"
            for (j in 0 until dimension) {
                str += ' ' + board[i * dimension + j].toString() + " |"
            }
            str += "\n"
        }
        return str
    }

    private operator fun List<Panel>.get(shipType: ShipType) =
        board.filter { it.shipType == shipType }

    operator fun get(coordinate: Coordinate) =
        coordinate.checkValid(dimension)
        .let { board[coordinate] }



    /**
     * Check if list of panel is sunk in case every panel is hit
     */
    private fun List<Panel>.checkSunk() = this.all { it.isHit }


    /**
     * Place a ship with given set of coordinates and a ship type
     * @return a new Board with the panels affected
     */
    fun placeShip(cs : CoordinateSet, shipType: ShipType) =
        Board(board.toMutableList().apply {
            cs.forEach {
                this[getIdx(it)] = Panel(it, shipType)
            }
        })

    /**
     * Places a list of panels in the board
     * @return a new Board with the panels affected
     */
    fun placePanels(panel : List<Panel>) =
        Board(board.toMutableList().apply {
            panel.forEach {
                this[getIdx(it.coordinate)] = it
            }
        })

    /**
     * Places a set of coordinates as water panels
     * @return a new Board with the panels affected
     */
    fun placeWaterPanel(cs : CoordinateSet) =
        Board(board.toMutableList().apply {
            cs.forEach {
                this[getIdx(it)] = Panel(it, null, this[it].isHit)
            }
        })

    /**
     * Check if coordinate is a ship
     */
    fun isShip(c: Coordinate) = board[c].isShip()

    /**
     * Place a shot in the board (change to hit the panel)
     * @return a new Board with the panels affected
     */
    fun placeShot(c : Coordinate) =
        Board(board.toMutableList().apply {
            this[getIdx(c)] = this[c].hit()
        })


    /**
     * Gets all ships from board
     */
    fun getShips() = ShipType.values().mapNotNull {
        getShipFromBoard(it)
    }.toSet()

    /**
     * Returns a ship from the board, given a specific ShipType
     */
    private fun getShipFromBoard(type : ShipType): Ship? {
        val coordinates = board[type]
        if (coordinates.isEmpty()) return null
        return Ship(coordinates.map {
            it.coordinate
        }.toSet(), type, coordinates.checkSunk())
    }


    /**
     * Get ship from a coordinate
     * @return a Ship
     * @throws IllegalArgumentException if no ship is found
     */
    fun getShipFromCoordinate(c : Coordinate) : Ship {
        val type = board[c].shipType ?: throw IllegalArgumentException("No ship found")
        return getShipFromBoard(type) ?: throw IllegalArgumentException("No ship found")
    }


}