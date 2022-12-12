package pt.isel.daw.dawbattleshipgame.domain.ship

import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Panel

enum class ShipType(private val icon: Char) {
    CARRIER('C'),
    BATTLESHIP('B'),
    CRUISER('K'),
    SUBMARINE('S'),
    DESTROYER('D');

    fun getIcon(isHit: Boolean) =
        if (isHit) this.icon.lowercaseChar()
        else this.icon

    companion object {
        fun get(icon: Char) = values().first {
            it.icon == icon.uppercaseChar()
        }
    }
}

fun Char.getPanel(c: Coordinate): Panel {
    return when (this) {
        Panel.WATER -> Panel(c)
        Panel.HIT -> Panel(c, isHit = true)
        else -> Panel(c, ShipType.get(this), this.isLowerCase())
    }
}

fun String.toShipType(): ShipType {
    return when (this.lowercase()) {
        "carrier" -> ShipType.CARRIER
        "battleship" -> ShipType.BATTLESHIP
        "cruiser" -> ShipType.CRUISER
        "submarine" -> ShipType.SUBMARINE
        "destroyer" -> ShipType.DESTROYER
        else -> throw IllegalArgumentException("Invalid ship type")
    }
}