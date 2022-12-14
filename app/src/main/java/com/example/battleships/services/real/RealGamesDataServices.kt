package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.game.domain.game.Game
import com.example.battleships.services.*
import com.example.battleships.utils.hypermedia.*
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class RealGamesDataServices(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): GameDataServices {

    private var createGameAction: SirenAction? = null
    private var getCurrentGameIdLink: SirenLink? = null
    private var getGameLink: SirenLink? = null
    private var placeShipsAction: SirenAction? = null
    private var placeShotAction: SirenAction? = null

    /**
     * Creates a new game.
     * @return true if the game was created successfully, if [newCreateGameAction] is needed.
     */
    override suspend fun createGame(token: String, mode: Mode, newGameCreateAction: SirenAction?): Either<Unit, Boolean> {
        val createGameAction = newGameCreateAction?.also { createGameAction = it }
            ?: this.createGameAction ?: return Either.Left(Unit)
        val url = createGameAction.href.toApiURL()

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
        val request = buildRequest(Post(url, requestBody), token, mode)

        val gameCreatedDto = request.send(httpClient) { response ->
            handleResponse<CreateGameDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type,
                SirenMediaType
            )
        }
        getCurrentGameIdLink = extractGetCurrentGameIdLink(gameCreatedDto) // could be null if game is already started
        getGameLink =
            extractGetGameLinkFromCreateGameDto(gameCreatedDto) // could be null if game still hasn't started
        return Either.Right(true)
    }

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        newSetFleetAction: SirenAction?,
        mode: Mode
    ): Either<Unit, Boolean> {
        val placeFleetLayout = newSetFleetAction?.also { placeShipsAction = it }
            ?: this.placeShipsAction ?: return Either.Left(Unit)
        val placeShipsUrl = placeFleetLayout.href.toApiURL()

        val requestBody = "{\n" +
            "\"operation\": \"place-ships\",\n" +
                    "    \"ships\": [\n" +
                    ships.joinToString(",\n") { ship ->
                        "        {\n" +
                                "            \"shipType\": \"${ship.first}\",\n" +
                                "            \"position\": {\n" +
                                "                \"row\": ${ship.second.row},\n" +
                                "                \"column\": ${ship.second.column}\n" +
                                "            },\n" +
                                "            \"orientation\": \"${ship.third}\"\n" +
                                "        }"
                    } +
                    "    ],\n" +
                    "\"fleetConfirmed\": \"true\"" +
                    "}"
        val request = buildRequest(Post(placeShipsUrl, requestBody), token, mode)

        request.send(httpClient) { response ->
            handleResponse<Unit>(
                jsonEncoder,
                response,
                Unit.javaClass,
                JsonMediaType
            )
        }
        return Either.Right(true)
    }

    override suspend fun placeShot(
        token: String,
        coordinate: Coordinate,
        newPlaceShotAction: SirenAction?,
        mode: Mode
    ): Either<Unit, Boolean> {
        val placeShotAction = newPlaceShotAction?.also { placeShotAction = it }
            ?: this.placeShotAction ?: return Either.Left(Unit)
        val placeShotUrl = placeShotAction.href.toApiURL()

        val body = "{\n" +
                "\t\"row\": ${coordinate.row},\n" +
                "\t\"column\": ${coordinate.column}\n" +
                "}"

        val request = buildRequest(Post(placeShotUrl, body), token, mode)

        request.send(httpClient) { response ->
            handleResponse<Unit>(
                jsonEncoder,
                response,
                Unit.javaClass,
                JsonMediaType
            )
        }
        return Either.Right(true)
    }

    override suspend fun getCurrentGameId(
        token: String,
        newGetCurrentGameIdLink: SirenLink?,
        mode: Mode
    ): Either<Unit, Int> {
        val getCurrentGameIdLink = newGetCurrentGameIdLink?.also { getCurrentGameIdLink = it }
            ?: this.getCurrentGameIdLink ?: return Either.Left(Unit)
        val url = getCurrentGameIdLink.href.toApiURL()

        val request = buildRequest(Get(url), token, mode)

        // TODO -> try catch to know if the game has already started
        val gameIdDto = request.send(httpClient) { response ->
            handleResponse<GameIdDto>(
                jsonEncoder,
                response,
                GameIdDtoType.type,
                SirenMediaType
            )
        }
        getGameLink = extractGetGameLinkFromGameIdDto(gameIdDto)
        return Either.Right(gameIdDto.toGameId())
    }

    override suspend fun getGame(
        token: String,
        newGetGameLink: SirenLink?,
        mode: Mode
    ): Either<Unit, Pair<Game, Player>?> {
        val getGameInfoLink = newGetGameLink?.also { getGameLink = it }
            ?: this.getGameLink ?: return Either.Left(Unit)
        val url = getGameInfoLink.href.toApiURL()

        val request = buildRequest(Get(url), token, mode)

        try {
            val gameDto = request.send(httpClient) { response ->
                handleResponse<GameDto>(
                    jsonEncoder,
                    response,
                    GameDtoType.type,
                    SirenMediaType
                )
            }
            placeShipsAction = extractPlaceFleetLayout(gameDto)
            placeShotAction = extractPlaceShotAction(gameDto)
            return Either.Right(gameDto.toGameAndPlayer())
        } catch (e: Exception) {
            return Either.Right(null)
        }
    }

    private fun extractPlaceFleetLayout(gameDto: GameDto) =
        gameDto.actions?.find { it.name == "place-ships" }

    private fun extractGetCurrentGameIdLink(gameCreatedDto: CreateGameDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-id") }

    private fun extractGetGameLinkFromCreateGameDto(gameCreatedDto: CreateGameDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-info") }

    private fun extractGetGameLinkFromGameIdDto(gameIdDto: GameIdDto) =
        gameIdDto.links?.find { it.rel.contains("game") }

    private fun extractPlaceShotAction(gameDto: GameDto) =
        gameDto.actions?.find { it.name == "place-shot" }

    suspend fun getGameLink(token: String, newGetCurrentGameIdLink: SirenLink): SirenLink {
        if (getGameLink == null) {
            getCurrentGameId(token, newGetCurrentGameIdLink, Mode.FORCE_REMOTE)
            return getGameLink ?: throw UnresolvedLinkException()
        }
        return getGameLink ?: throw UnresolvedLinkException()
    }

    suspend fun getSetFleetAction(token: String, newGetGameLink: SirenLink): SirenAction {
        if (placeShipsAction == null) {
            getGame(token, newGetGameLink, Mode.FORCE_REMOTE)
            return placeShipsAction ?: throw UnresolvedLinkException()
        }
        return placeShipsAction ?: throw UnresolvedLinkException()
    }

    suspend fun getPlaceShotAction(token: String, newGetGameLink: SirenLink): SirenAction {
        if (placeShotAction == null) {
            getGame(token, newGetGameLink, Mode.FORCE_REMOTE)
            return placeShotAction ?: throw UnresolvedLinkException()
        }
        return placeShotAction ?: throw UnresolvedLinkException()
    }
}