package com.example.battleships.services.fake

// TODO -> to be deleted later (this implemented all services but now, services were divided in multiple classes)
/*
class FakeBattleshipService : GameDataServices {
    private val games = mutableMapOf<Pair<String, String>, Game>()

    private val configuration = Configuration(
        boardSize = 10,
        fleet = setOf(
            Pair(ShipType.CARRIER, 5),
            Pair(ShipType.BATTLESHIP, 4),
            Pair(ShipType.CRUISER, 3),
            Pair(ShipType.SUBMARINE, 3),
            Pair(ShipType.DESTROYER, 2)
        ),
        nShotsPerRound = 10,
        roundTimeout = 10
    )

    override suspend fun getHome(): Home = Home("Welcome to the Battleships API")

    override fun getServerInfo() = ServerInfo(listOf(
        ServerAuthor("Miguel", "a47185@alunos.isel.pt"),
        // TODO
    ), "0.9")

    override suspend fun getGameId(token: String): Int? {
        val (token1, token2, game) = getGameAndTokens(token) ?: return null
        return game.player1 // according to startGame, its the player is always player1
    }

    override suspend fun startNewGame(token: String) {
        val gameId = 555
        val player1Id = 111
        val player2Id = 222
        val opponentToken = "opponentToken"
        val game = Game.newGame(gameId, player1Id, player2Id, configuration)
        games[token to opponentToken] = game
        placeOpponentShips(opponentToken)
    }

    private fun placeOpponentShips(token: String) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.FLEET_SETUP) {
            val board = game.board2
            if (!board.isConfirmed()) {
                val newGame = game.onSquarePressed(ShipType.CARRIER, Coordinate(1, 1), Orientation.HORIZONTAL, Player.TWO)
                    ?.onSquarePressed(ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL, Player.TWO)
                    ?.onSquarePressed(ShipType.CRUISER, Coordinate(5, 2), Orientation.HORIZONTAL, Player.TWO)
                    ?.onSquarePressed(ShipType.SUBMARINE, Coordinate(7, 3), Orientation.HORIZONTAL, Player.TWO)
                    ?.onSquarePressed(ShipType.DESTROYER, Coordinate(9, 4), Orientation.HORIZONTAL, Player.TWO)
                    ?.confirmFleet(Player.TWO) ?: throw IllegalStateException("Opponent fleet not placed")

                games[token1 to token2] = newGame
            }
        }
    }

    override suspend fun placeShip(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.FLEET_SETUP) {
            val board = game.board1
            if (!board.isConfirmed()) {
                val newGame = game.onSquarePressed(shipType, coordinate, orientation, Player.ONE)
                if (newGame != null) {
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    override suspend fun moveShip(token: String, gameId: Int, origin: Coordinate, destination: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.FLEET_SETUP) {
            val board = game.board1
            if (!board.isConfirmed()) {
                val newGame = game.moveShip(origin, destination, Player.ONE)
                if (newGame != null) {
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    override suspend fun rotateShip(token: String, gameId: Int, position: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.FLEET_SETUP) {
            val board = game.board1
            if (!board.isConfirmed()) {
                val newGame = game.rotateShip(position, Player.ONE)
                if (newGame != null) {
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    /**
     * Places a shot in enemy fleet, in case is player 1 turn.
     * After that, it places a shot in player 1 fleet, just for test purposes.
     */
    override suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.BATTLE) {
            val newGame = game.placeShot(game.player1, coordinate, Player.TWO)
            if (newGame != null)
                games[token1 to token2] = newGame
        }
        letOpponentPlaceShotOnMe(token2)
    }

    private fun letOpponentPlaceShotOnMe(token: String) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.BATTLE) {
            val randomCoordinate = Coordinate((1..configuration.boardSize).random(), (1..configuration.boardSize).random())
            val newGame = game.placeShot(game.player2, randomCoordinate, Player.TWO)
            if (newGame != null)
                games[token1 to token2] = newGame
            else
                throw IllegalStateException("Opponent shot not placed")
        }
    }

    override suspend fun confirmFleet(token: String, gameId: Int) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game.state === GameState.FLEET_SETUP) {
            val myBoard = game.board1
            val opponentBoard = game.board2
            if (!myBoard.isConfirmed() && opponentBoard.isConfirmed()) {
                val newGame = Game(
                    game.gameId,
                    game.configuration,
                    game.player1,
                    game.player2,
                    game.board1.confirm(),
                    game.board2.confirm(),
                    game.state,
                    game.playerTurn,
                    game.winner
                )
                games[token1 to token2] = newGame
            }
        }
    }

    override suspend fun getGame(token: String, gameId: Int): Game? {
        return getGameAndTokens(token)?.third ?: return null
    }

    private fun getGameAndTokens(token: String): Triple<String, String, Game>? {
        val (token1, token2) = games
            .filterKeys { it.first == token || it.second == token }
            .keys.firstOrNull() ?: return null
        val localGame = games[token1 to token2] ?: return null
        return Triple(token1, token2, localGame)
    }
}
 */