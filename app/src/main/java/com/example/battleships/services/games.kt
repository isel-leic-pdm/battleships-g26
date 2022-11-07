package com.example.battleships.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.dtos.HomeDto
import com.example.battleships.dtos.HomeDtoType
import com.example.battleships.dtos.RankingsDtoType
import com.example.battleships.dtos.ServerInfoDtoType
import com.example.battleships.game.BoardDtoType
import com.example.battleships.game.GameDtoType
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Game
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.info.serverInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.example.battleships.utils.send
import okhttp3.Request
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import java.net.URL

/**
 * @see rankingsLink
 */
private var startNewGameLink: SirenLink? = null
private var getCurrentGameIdLink: SirenLink? = null
private var placeShipLink: SirenLink? = null
private var moveShipLink: SirenLink? = null
private var rotateShipLink: SirenLink? = null
private var confirmFleetLayoutLink: SirenLink? = null
private var fireShotLink: SirenLink? = null

internal suspend fun startNewGameInternal(token: String) {
    TODO("Not yet implemented")
}

internal suspend fun getGameIdInternal(token: String): Int? {
    val request = Request.Builder()
        .url(getGameIdUri)
        .build()

    val gameIdDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        val contentType = response.body?.contentType()
        if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
            jsonEncoder.fromJson<GameIdDto>(
                response.body?.string(),
                GameDtoType.type
            )
        }
        else {
            Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
            TODO()
        }
    }
    Log.v(TAG, "fetchQuote: after request.send in Thread = ${Thread.currentThread().name}")
    return gameIdDto.properties?.gameId
}

internal suspend fun placeShipInternal(
    token: String,
    gameId: Int,
    shipType: ShipType,
    coordinate: Coordinate,
    orientation: Orientation
) {
    val request = Request.Builder()
        .url(getPlaceShipUri(gameId))
        .build()
    val quoteDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
    }
}

internal suspend fun moveShipInternal(token: String, gameId: Int, origin: Coordinate, destination: Coordinate) {
    val request = Request.Builder()
        .url(getMoveShipUri(gameId))
        .build()
    val quoteDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
    }
}

internal suspend fun rotateShipInternal(token: String, gameId: Int, position: Coordinate) {
    val request = Request.Builder()
        .url(getRotateShipUri(gameId))
        .build()
    val quoteDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
    }
}

internal suspend fun placeShotInternal(token: String, gameId: Int, coordinate: Coordinate) {
    val request = Request.Builder()
        .url(getPlaceShotUri(gameId))
        .build()
    val quoteDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
    }
}

internal suspend fun confirmFleetInternal(token: String, gameId: Int) {
    val request = Request.Builder()
        .url(getConfirmFleetUri(gameId))
        .build()
    val quoteDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
    }
}

internal suspend fun getMyFleetInternal(token: String, gameId: Int): Board? {
    val request = Request.Builder()
        .url(getMyFleetUri(gameId))
        .build()
    val boardDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        val contentType = response.body?.contentType()
        if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
            jsonEncoder.fromJson<BoardDto>(
                response.body?.string(),
                BoardDtoType.type
            )
        }
        else {
            Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
            TODO()
        }
    }
    return boardDto.properties?.toBoard()
}

Internal suspend fun getEnemyFleetInternal(token: String, gameId: Int): Board? {
    val request = Request.Builder()
        .url(getOpponentFleetUri(gameId))
        .build()
    val boardDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        val contentType = response.body?.contentType()
        if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
            jsonEncoder.fromJson<BoardDto>(
                response.body?.string(),
                BoardDtoType.type
            )
        }
        else {
            Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
            TODO()
        }
    }
    return boardDto.properties?.toBoard()
}

internal suspend fun getGameInternal(token: String, gameId: Int): Game? {
    val request = Request.Builder()
        .url(getGameInfoUri(gameId))
        .build()
    val gameDto = request.send(httpClient) { response ->
        Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        val contentType = response.body?.contentType()
        if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
            jsonEncoder.fromJson<GameDto>(
                response.body?.string(),
                BoardDtoType.type
            )
        }
        else {
            Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
            TODO()
        }
    }
    return gameDto.properties?.toGame()
}