package com.example.battleships.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.dtos.HomeDto
import com.example.battleships.dtos.HomeDtoType
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
import com.example.battleships.game.services.BattleshipsService
import com.example.battleships.home.Home
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

    /* --------------------------------------- HOME ------------------------------------------------- */

    override suspend fun fetchHome(mode: BattleshipsService.Mode): Home {
        val request = buildRequest(url = battleshipsHome, mode = mode)

        val homeDto = request.send(httpClient) { response ->
            handleResponse<HomeDto>(response, HomeDtoType.type)
        }

        rankingsLink = getRankingsLink(homeDto)
        if (rankingsLink == null)
            throw UnresolvedLinkException()

        return Home(homeDto)
    }

    /**
     * The link for the APIs resource bearing the week's quotes, or null if not
     * yet discovered.
     */
    private var rankingsLink: SirenLink? = null

    /**
     * Navigates [home] in search of the link for the APIs resource
     * bearing the week's quotes.
     * @return the link found in the DTO, or null
     */
    private fun getRankingsLink(home: HomeDto) =
        home.links?.find { it.rel.contains("user-stats") }

    /**
     * Makes sure we have the required link, if necessary, by navigating again
     * through the APIs responses, starting at the home resource (the home page, in this case).
     *
     * @return the [URL] instance representing the link for the week's quotes
     * @throws [UnresolvedLinkException] if the link could not be found
     */
    private suspend fun ensureRankingsLink(): URL {
        if (rankingsLink == null) {
            fetchQuote()
        }
        val link = weekQuotesLink ?: throw UnresolvedLinkException()
        return link.href.toURL()
    }

    /* --------------------------------------- HOME ------------------------------------------------- */

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



    /**
     * Builds a request.
     */
    private fun buildRequest(url: URL, mode: QuoteService.Mode) =
        with(Request.Builder()) {
            when(mode) {
                QuoteService.Mode.FORCE_REMOTE -> cacheControl(CacheControl.FORCE_NETWORK)
                QuoteService.Mode.FORCE_LOCAL -> cacheControl(CacheControl.FORCE_CACHE)
                else -> this
            }
        }.url(url).build()

    /**
     * This method's usefulness is circumstantial. In more realistic scenarios
     * we will not be handling API responses with this simplistic approach.
     */
    private fun <T> handleResponse(response: Response, type: Type): T {
        val contentType = response.body?.contentType()
        return if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
            try {
                val body = response.body?.string()
                jsonEncoder.fromJson<T>(body, type)
            }
            catch (e: JsonSyntaxException) {
                throw UnexpectedResponseException(response)
            }
        }
        else {
            throw UnexpectedResponseException(response = response)
        }
    }
}