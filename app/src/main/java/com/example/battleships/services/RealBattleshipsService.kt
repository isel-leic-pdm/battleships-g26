package com.example.battleships.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.dtos.*
import com.example.battleships.game.*
import com.example.battleships.game.BoardDto
import com.example.battleships.game.BoardDtoType
import com.example.battleships.game.GameDto
import com.example.battleships.game.GameDtoType
import com.example.battleships.game.GameIdDto
import com.example.battleships.game.domain.state.Game
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.battleships.game.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.info.serverInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.utils.hypermedia.SirenLink
import com.google.gson.JsonSyntaxException
import okhttp3.CacheControl
import okhttp3.Response
import java.lang.reflect.Type
import java.net.URL

class RealBattleshipsService(
    private val battleshipsHome: URL,
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): BattleshipsService {
    override suspend fun getRankings() = getRankingsInternal()

    override suspend fun getServerInfo() = getServerInfoInternal()

    override fun createUser(username: String, password: String) = createUserInternal(username, password)

    override suspend fun login(username: String, password: String) = doLoginInternal(username, password)

    override suspend fun getGameId(token: String) = getGameIdInternal(token)

    override suspend fun startNewGame(token: String) = startNewGameInternal(token)

    override suspend fun placeShip(
        token: String,
        gameId: Int,
        shipType: ShipType,
        coordinate: Coordinate,
        orientation: Orientation
    ) = placeShipInternal(token, gameId, shipType, coordinate, orientation)

    override suspend fun moveShip(
        token: String,
        gameId: Int,
        origin: Coordinate,
        destination: Coordinate
    ) = moveShipInternal(token, gameId, origin, destination)

    override suspend fun rotateShip(token: String, gameId: Int, position: Coordinate) =
        rotateShipInternal(token, gameId, position)

    override suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate) =
        placeShotInternal(token, gameId, coordinate)

    override suspend fun confirmFleet(token: String, gameId: Int) = confirmFleetInternal(token, gameId)

    override suspend fun getGame(token: String, gameId: Int) = getGameInternal(token, gameId)

}