package com.example.battleships.game.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Game
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class RealBattleshipsService(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): BattleshipsService {
    private val getGameIdUri = URL("/games/current")

    override suspend fun getUserId(token: String): Int? {
        val request = Request.Builder()
            .url(getGameIdUri)
            .build()
        
        val quoteDto = request.send(httpClient) { response ->
            Log.v(TAG, "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}")
            val contentType = response.body?.contentType()
            if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
                jsonEncoder.fromJson<QuoteDto>(
                    response.body?.string(),
                    QuoteDtoType.type
                )
            }
            else {
                Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
                TODO()
            }
        }

        Log.v(TAG, "fetchQuote: after request.send in Thread = ${Thread.currentThread().name}")
        return Quote(quoteDto)
    }

    override suspend fun startNewGame(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun placeShip(
        token: String,
        shipType: ShipType,
        coordinate: Coordinate,
        orientation: Orientation
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun moveShip(token: String, origin: Coordinate, destination: Coordinate) {
        TODO("Not yet implemented")
    }

    override suspend fun rotateShip(token: String, position: Coordinate) {
        TODO("Not yet implemented")
    }

    override suspend fun placeShot(token: String, coordinate: Coordinate) {
        TODO("Not yet implemented")
    }

    override suspend fun confirmFleet(token: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getGameState(token: String): Game? {
        TODO("Not yet implemented")
    }

}