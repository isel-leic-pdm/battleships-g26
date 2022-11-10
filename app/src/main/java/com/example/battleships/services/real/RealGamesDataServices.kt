package com.example.battleships.services.real

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.dtos.*
import com.example.battleships.game.BoardDtoType
import com.example.battleships.game.GameInfo
import com.example.battleships.game.GameDtoType
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.state.Game
import com.example.battleships.services.*
import com.example.battleships.services.buildRequest
import com.example.battleships.services.handleResponse
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import java.net.URL

class RealGamesDataServices(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): GameDataServices {

    /**
     * @see rankingsLink
     */
    private var createGameAction: SirenAction? = null
    private var getCurrentGameIdLink: SirenLink? = null
    private var getGameLink: SirenLink? = null
    private var placeFleetLayout: SirenAction? = null
    private var placeShotAction: SirenAction? = null

    /**
     * Creates a new game.
     * @return The GameInfo of the newly created game, or null if needs [createGameAction].
     */
    override suspend fun createGame(token: String, mode: Mode, newGameCreateAction: SirenAction?): GameInfo? {
        val createGameAction = newGameCreateAction?.also { createGameAction = it }
            ?: this.createGameAction ?: return null
        val url = createGameAction.href.toURL()

        // TODO -> parameterize the game configuration
        val requestBody = "{\n" +
                "    \"boardSize\": 10,\n" +
                "    \"fleet\": {\n" +
                "        \"CARRIER\": 5,\n" +
                "        \"BATTLESHIP\": 4,\n" +
                "        \"CRUISER\": 3,\n" +
                "        \"SUBMARINE\": 3,\n" +
                "        \"DESTROYER\": 2\n" +
                "    },\n" +
                "    \"nShotsPerRound\": 10,\n" +
                "    \"roundTimeout\": 10\n" +
                "}"
        val request = buildRequest(Post(url, requestBody), mode)

        val gameCreatedDto = request.send(httpClient) { response ->
            handleResponse<GameInfoDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type
            )
        }

        getCurrentGameIdLink = getGetCurrentGameIdLink(gameCreatedDto) // could be null if game is already started
        getGameLink = getGetGameLink(gameCreatedDto) // could be null if game still hasn't started

        return gameCreatedDto.toGameInfo()
    }

    override suspend fun getCurrentGameId(
        token: String,
        mode: Mode,

    ): Int? {
        val request = Request.Builder()
            .url(getGameIdUri)
            .build()

        val gameIdDto = request.send(httpClient) { response ->
            Log.v(
                TAG,
                "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}"
            )
            val contentType = response.body?.contentType()
            if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
                jsonEncoder.fromJson<GameIdDto>(
                    response.body?.string(),
                    GameDtoType.type
                )
            } else {
                Log.e(
                    TAG,
                    "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?"
                )
                TODO()
            }
        }
        Log.v(TAG, "fetchQuote: after request.send in Thread = ${Thread.currentThread().name}")
        return gameIdDto.properties?.gameId
    }

    internal suspend fun placeShotInternal(
        mode: Mode,
        token: String,
        gameId: Int,
        coordinate: Coordinate
    ): GameInfo {
        val placeShotLink: URL = ensurePlaceShotAction(requestParams)

        val requestBody = "{\n" +
                "    \"coordinate\": \"${coordinate.row},${coordinate.column}\"\n" +
                "}"
        val request = buildRequest(
            Post(
                placeShotLink,
                requestBody
            ), mode
        )

        return request.send(httpClient) { response ->
            handleResponse<GameInfoDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type
            )
        }.toGameInfo()
    }

    internal suspend fun getMyFleetInternal(
        requestParams: RequestParams,
        token: String,
        gameId: Int
    ): Board? {
        val request = Request.Builder()
            .url(getMyFleetUri(gameId))
            .build()
        val boardDto = request.send(httpClient) { response ->
            Log.v(
                TAG,
                "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}"
            )
            val contentType = response.body?.contentType()
            if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
                jsonEncoder.fromJson<BoardDto>(
                    response.body?.string(),
                    BoardDtoType.type
                )
            } else {
                Log.e(
                    TAG,
                    "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?"
                )
                TODO()
            }
        }
        return boardDto.properties?.toBoard()
    }

    suspend fun getEnemyFleetInternal(token: String, gameId: Int): Board? {
        val request = Request.Builder()
            .url(getOpponentFleetUri(gameId))
            .build()
        val boardDto = request.send(httpClient) { response ->
            Log.v(
                TAG,
                "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}"
            )
            val contentType = response.body?.contentType()
            if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
                jsonEncoder.fromJson<BoardDto>(
                    response.body?.string(),
                    BoardDtoType.type
                )
            } else {
                Log.e(
                    TAG,
                    "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?"
                )
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
            Log.v(
                TAG,
                "fetchQuote: inside response handler in Thread = ${Thread.currentThread().name}"
            )
            val contentType = response.body?.contentType()
            if (response.isSuccessful && contentType != null && contentType == SirenMediaType) {
                jsonEncoder.fromJson<GameDto>(
                    response.body?.string(),
                    BoardDtoType.type
                )//12.30
            } else {
                Log.e(
                    TAG,
                    "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?"
                )
                TODO()
            }
        }
        return gameDto.properties?.toGame()
    }

    private fun getGetCurrentGameIdLink(gameCreatedDto: GameInfoDto): SirenLink? {
        return gameCreatedDto.links?.find { it.rel.contains("game-id") }
    }

    private fun getGetGameLink(gameCreatedDto: GameInfoDto): SirenLink? {
        return gameCreatedDto.links?.find { it.rel.contains("game-info") }
    }

}