package com.example.battleships.game.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.game.*
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
import java.net.URL

class RealBattleshipsService(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): BattleshipsService {
    private val getGameIdUri = URL("/games/current")

    private fun getPlaceShipUri(gameId: Int) = URL("/games/$gameId/place-ship")
    private fun getMoveShipUri(gameId: Int) = URL("/games/$gameId/move-ship")
    private fun getRotateShipUri(gameId: Int) = URL("/games/$gameId/rotate-ship")
    private fun getConfirmFleetUri(gameId: Int) = URL("/games/$gameId/confirm-fleet")
    private fun getPlaceShotUri(gameId: Int) = URL("/games/$gameId/place-shot")

    private fun getMyFleetUri(gameId: Int) = URL("/games/$gameId/my-fleet")
    private fun getOpponentFleetUri(gameId: Int) = URL("/games/$gameId/opponent-fleet")
    private fun getGameInfoUri(gameId: Int) = URL("/games/$gameId")

    override suspend fun startNewGame(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getGameId(token: String): Int? {
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

    override suspend fun placeShip(
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

    override suspend fun moveShip(token: String, gameId: Int, origin: Coordinate, destination: Coordinate) {
        val request = Request.Builder()
            .url(getMoveShipUri(gameId))
            .build()
        val quoteDto = request.send(httpClient) { response ->
            Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        }
    }

    override suspend fun rotateShip(token: String, gameId: Int, position: Coordinate) {
        val request = Request.Builder()
            .url(getRotateShipUri(gameId))
            .build()
        val quoteDto = request.send(httpClient) { response ->
            Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        }
    }

    override suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate) {
        val request = Request.Builder()
            .url(getPlaceShotUri(gameId))
            .build()
        val quoteDto = request.send(httpClient) { response ->
            Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        }
    }

    override suspend fun confirmFleet(token: String, gameId: Int) {
        val request = Request.Builder()
            .url(getConfirmFleetUri(gameId))
            .build()
        val quoteDto = request.send(httpClient) { response ->
            Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
        }
    }

    private suspend fun getMyFleet(token: String, gameId: Int): Board? {
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

    private suspend fun getEnemyFleet(token: String, gameId: Int): Board? {
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

    override suspend fun getGame(token: String, gameId: Int): Game? {
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

}