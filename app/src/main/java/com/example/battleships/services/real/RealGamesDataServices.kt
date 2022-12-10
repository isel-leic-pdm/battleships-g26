package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.game.domain.game.Game
import com.example.battleships.services.*
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.utils.hypermedia.SirenLink
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

    /**
     * @see rankingsLink
     */
    private var createGameAction: SirenAction? = null
    private var getCurrentGameIdLink: SirenLink? = null
    private var getGameLink: SirenLink? = null
    private var placeShipsAction: SirenAction? = null
    private var placeShotAction: SirenAction? = null
    private var confirmFleetLayoutAction: SirenAction? = null

    /**
     * Creates a new game.
     * @return true if the game was created successfully, if [newCreateGameAction] is needed.
     */
    override suspend fun createGame(token: String, mode: Mode, newGameCreateAction: SirenAction?): Either<Unit, Boolean> {
        val createGameAction = newGameCreateAction?.also { createGameAction = it }
            ?: this.createGameAction ?: return Either.Left(Unit)
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
            handleResponse<CreateGameDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type
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
        val placeShipsUrl = placeFleetLayout.href.toURL()

        val requestBody = ships.joinToString(prefix = "[", postfix = "]") { (shipType, coordinate) ->
            "{\n" +
                    "    \"type\": \"${shipType.name}\",\n" +
                    "    \"position\": {\n" +
                    "        \"x\": ${coordinate.row},\n" +
                    "        \"y\": ${coordinate.column}\n" +
                    "    }\n" +
                    "}"
        }
        val request = buildRequest(Post(placeShipsUrl, requestBody), mode)

        request.send(httpClient) { response ->
            handleResponse<Unit>(
                jsonEncoder,
                response,
                Unit.javaClass
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
        val placeShotUrl = placeShotAction.href.toURL()

        val body = "{\n" +
                "\t\"x\": ${coordinate.row},\n" +
                "\t\"y\": ${coordinate.column}\n" +
                "}"

        val request = buildRequest(Post(placeShotUrl, body), mode)

        request.send(httpClient) { response ->
            handleResponse<Unit>(
                jsonEncoder,
                response,
                Unit.javaClass
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
        val url = getCurrentGameIdLink.href.toURL()

        val request = buildRequest(Get(url), mode)

        // TODO -> try catch to know if the game has already started
        val gameIdDto = request.send(httpClient) { response ->
            handleResponse<GameIdDto>(
                jsonEncoder,
                response,
                GameIdDtoType.type
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
        val url = getGameInfoLink.href.toURL()

        val request = buildRequest(Get(url), mode)

        val gameDto = request.send(httpClient) { response ->
            handleResponse<GameDto>(
                jsonEncoder,
                response,
                GameInfoDtoType.type
            )
        }
        placeShipsAction = getPlaceFleetLayout(gameDto)
        confirmFleetLayoutAction = getConfirmFleetLayout(gameDto)
        TODO("Not yet implemented: needs to know wich player is acossiated withe token")
        // return gameDto.toGame()
    }

    private fun getConfirmFleetLayout(gameDto: SirenEntity<GameDtoProperties>) =
        gameDto.actions?.find { it.name == "confirm-fleet-layout" }

    private fun getPlaceFleetLayout(gameDto: GameDto) =
        gameDto.actions?.find { it.name == "place-fleet" }

    private fun extractGetCurrentGameIdLink(gameCreatedDto: CreateGameDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-id") }

    private fun extractGetGameLinkFromCreateGameDto(gameCreatedDto: CreateGameDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-info") }

    private fun extractGetGameLinkFromGameIdDto(gameIdDto: GameIdDto) =
        gameIdDto.links?.find { it.rel.contains("game-info") }

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