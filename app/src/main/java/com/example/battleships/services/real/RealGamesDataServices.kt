package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.services.*
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import com.example.battleships.game.domain.game.Game
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
    override suspend fun createGame(token: String, mode: Mode, newGameCreateAction: SirenAction?): Boolean {
        val createGameAction = newGameCreateAction?.also { createGameAction = it }
            ?: this.createGameAction ?: return false
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
        getGameLink = getGetGameLinkByGameInfoDto(gameCreatedDto) // could be null if game still hasn't started
        return true
    }

    override suspend fun getCurrentGameId(
        token: String,
        mode: Mode,
        newGetCurrentGameIdLink: SirenLink?
    ): Int? {
        val getCurrentGameIdLink = newGetCurrentGameIdLink?.also { getCurrentGameIdLink = it }
            ?: this.getCurrentGameIdLink ?: return null
        val url = getCurrentGameIdLink.href.toURL()

        val request = buildRequest(Get(url), mode)

        val gameIdDto = request.send(httpClient) { response ->
            handleResponse<GameIdDto>(
                jsonEncoder,
                response,
                GameIdDtoType.type
            )
        }
        getGameLink = getGetGameLinkByGameIdDto(gameIdDto)
        return gameIdDto.toGameId()
    }

    /**
     * Sets the fleet of the current game.
     * @return True if the fleet was set, false if needs [placeShipsAction].
     */
    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        newSetFleetAction: SirenAction?,
        mode: Mode
    ): Boolean {
        val placeFleetLayout = newSetFleetAction?.also { placeShipsAction = it }
            ?: this.placeShipsAction ?: return false
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
        return true
    }

    override suspend fun confirmFleetLayout(
        token: String,
        mode: Mode,
        newConfirmFleetLayoutAction: SirenAction?
    ): Boolean {
        val confirmFleetLayoutAction = newConfirmFleetLayoutAction?.also { confirmFleetLayoutAction = it }
            ?: this.confirmFleetLayoutAction ?: return false
        val confirmFleetLayoutUrl = confirmFleetLayoutAction.href.toURL()

        val body = "{\n" +
                "\t\"fleetConfirmed\": \"true\"\n" +
                "}"

        val request = buildRequest(Put(confirmFleetLayoutUrl, body), mode)

        request.send(httpClient) { response ->
            handleResponse<Unit>(
                jsonEncoder,
                response,
                Unit.javaClass
            )
        }
        return true
    }

    override suspend fun placeShot(
        token: String,
        coordinate: Coordinate,
        newPlaceShotAction: SirenAction?,
        mode: Mode
    ): Boolean {
        val placeShotAction = newPlaceShotAction?.also { placeShotAction = it }
            ?: this.placeShotAction ?: return false
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
        return true
    }

    override suspend fun getGame(
        token: String,
        newGetGameLink: SirenLink?,
        mode: Mode
    ): Pair<Game, Player>? {
        val getGameInfoLink = newGetGameLink?.also { getGameLink = it }
            ?: this.getGameLink ?: return null
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

    private fun getGetCurrentGameIdLink(gameCreatedDto: GameInfoDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-id") }

    private fun getGetGameLinkByGameInfoDto(gameCreatedDto: GameInfoDto) =
        gameCreatedDto.links?.find { it.rel.contains("game-info") }

    private fun getGetGameLinkByGameIdDto(gameIdDto: GameIdDto) =
        gameIdDto.links?.find { it.rel.contains("game-info") }
}