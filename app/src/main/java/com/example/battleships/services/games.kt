package com.example.battleships.services

import android.util.Log
import com.example.battleships.TAG
import com.example.battleships.dtos.*
import com.example.battleships.game.BoardDtoType
import com.example.battleships.game.GameAction
import com.example.battleships.game.GameDtoType
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Game
import com.example.battleships.utils.hypermedia.SirenAction
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
internal var startNewGameAction: SirenAction? = null
private var getCurrentGameIdLink: SirenLink? = null
private var placeShipAction: SirenAction? = null
private var moveShipAction: SirenAction? = null
private var rotateShipAction: SirenAction? = null
private var placeShotAction: SirenAction? = null
private var confirmFleetLayoutAction: SirenAction? = null
private var fireShotAction: SirenAction? = null

internal suspend fun startNewGameInternal(requestParams: RequestParams, token: String) {
    val startNewGameLink: URL = ensureStartGameAction(requestParams)

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
    val request = buildRequest(Post(startNewGameLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun getGameIdInternal(requestParams: RequestParams, token: String): Int? {
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
    requestParams: RequestParams,
    token: String,
    gameId: Int,
    shipType: ShipType,
    coordinate: Coordinate,
    orientation: Orientation,
): GameAction {
    val startNewGameLink: URL = ensureStartGameAction(requestParams)

    val requestBody = "{\n" +
            "    \"shipType\": \"${shipType.name}\",\n" +
            "    \"coordinate\": \"${coordinate.row},${coordinate.column}\",\n" +
            "    \"orientation\": \"${orientation.name}\"\n" +
            "}"
    val request = buildRequest(Post(startNewGameLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun moveShipInternal(
    requestParams: RequestParams,
    token: String,
    gameId: Int,
    origin: Coordinate,
    destination: Coordinate
): GameAction {
    val moveShipLink: URL = ensureMoveShipAction(requestParams)

    val requestBody = "{\n" +
            "    \"origin\": \"${origin.row},${origin.column}\",\n" +
            "    \"destination\": \"${destination.row},${destination.column}\"\n" +
            "}"
    val request = buildRequest(Post(moveShipLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun rotateShipInternal(requestParams: RequestParams, token: String, gameId: Int, position: Coordinate) {
    val rotateShipLink: URL = ensureRotateShipAction(requestParams)

    val requestBody = "{\n" +
            "    \"position\": \"${position.row},${position.column}\"\n" +
            "}"
    val request = buildRequest(Post(rotateShipLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun placeShotInternal(
    requestParams: RequestParams,
    token: String,
    gameId: Int,
    coordinate: Coordinate
): GameAction {
    val placeShotLink: URL = ensurePlaceShotAction(requestParams)

    val requestBody = "{\n" +
            "    \"coordinate\": \"${coordinate.row},${coordinate.column}\"\n" +
            "}"
    val request = buildRequest(Post(placeShotLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun confirmFleetInternal(requestParams: RequestParams, token: String, gameId: Int): GameAction {
    val confirmFleetLink: URL = ensureConfirmFleetAction(requestParams)

    val requestBody = "{}"
    val request = buildRequest(Post(confirmFleetLink,
        requestBody), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<GameActionDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toGameAction()
}

internal suspend fun getMyFleetInternal(requestParams: RequestParams, token: String, gameId: Int): Board? {
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
            )//12.30
        }
        else {
            Log.e(TAG, "fetchQuote: Got response status ${response.code} from API. Is the home URL correct?")
            TODO()
        }
    }
    return gameDto.properties?.toGame()
}


private suspend fun ensureStartGameAction(requestParams: RequestParams): URL {
    if (userCreateAction == null) {
        getHome(requestParams)
    }
    val action = userCreateAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

private suspend fun ensurePlaceShipAction(requestParams: RequestParams): URL {
    if (placeShipAction == null) {
        getHome(requestParams)
    }
    val action = placeShipAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

private suspend fun ensureMoveShipAction(requestParams: RequestParams): URL {
    if (moveShipAction == null) {
        getHome(requestParams)
    }
    val action = moveShipAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

private suspend fun ensureRotateShipAction(requestParams: RequestParams): URL {
    if (rotateShipAction == null) {
        getHome(requestParams)
    }
    val action = rotateShipAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

private suspend fun ensurePlaceShotAction(requestParams: RequestParams): URL {
    if (placeShotAction == null) {
        getHome(requestParams)
    }
    val action = placeShotAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

private suspend fun ensureConfirmFleetAction(requestParams: RequestParams): URL {
    if (confirmFleetLayoutAction == null) {
        getHome(requestParams)
    }
    val action = confirmFleetLayoutAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}