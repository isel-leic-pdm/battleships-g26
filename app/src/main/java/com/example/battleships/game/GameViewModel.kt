package com.example.battleships.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.battleships.UseCases
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val useCases: UseCases,
    private val token: String
) : ViewModel() {
    private val _userId: MutableState<Int?> = mutableStateOf(null)
    val userId: State<Int?>
        get() = _userId

    private var _gameId: MutableState<Int?> = mutableStateOf(null)
    val gameId: State<Int?>
        get() = _gameId

    private var _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game

    private val _myBoardDisplayed: MutableState<Boolean> = mutableStateOf(true)
    val myBoardDisplayed: State<Boolean>
        get() = _myBoardDisplayed

    private var _player: MutableState<Player?> = mutableStateOf(null)
    val player: State<Player?>
        get() = _player


    fun startGame() {
        /*
        viewModelScope.launch {
            useCases.createGame(token) // requests to start a new game
            _gameId.value = useCases.getGameId(token) // asserts gameId
            setGame() // asserts game
        }

         */
    }

    private fun setGame() {
        /*
        val gameId = _gameId.value
        if (gameId != null) {
            viewModelScope.launch {
                while (game.value == null) {
                    delay(1000)
                    _game.value = useCases.getGame(token, gameId)
                }
                setPlayer() // asserts player
            }
        }
         */
    }

    private fun setPlayer() {
        val gameInternal = game.value ?: return
        val userId = _userId.value ?: return
        if (gameInternal.player1 == userId) {
            _player.value = Player.ONE
        }
        _player.value = Player.TWO
    }

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        /*
        val gameInternal = game.value ?: return
        val player = player.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.onSquarePressed(shipType, coordinate, orientation, player)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    useCases.placeShip(token, gameId, shipType, coordinate, orientation)
                    _game.value = useCases.getGame(token, gameId)
                }
            }
        }
         */
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.moveShip(origin, destination)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    useCases.moveShip(token, gameId, origin, destination)
                    _game.value = useCases.getGame(token, gameId)
                }
            }
        }
         */
    }

    fun rotateShip(position: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.rotateShip(position)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    useCases.rotateShip(token, gameId, position)
                    _game.value = useCases.getGame(token, gameId) ?: _game.value
                }
            }
        }
         */
    }

    fun confirmFleet() {
        /*
        val gameInternal = game.value ?: return
        val player = player.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.confirmFleet(player)
                viewModelScope.launch {
                    useCases.confirmFleet(token, gameId)
                    _game.value = useCases.getGame(token, gameId) ?: _game.value
                }
            }
        }
         */
    }

    fun placeShot(coordinate: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.BATTLE) {
            gameInternal.placeShot(gameInternal.player1, coordinate) // tests if the action is valid
                ?: return
            viewModelScope.launch {
                useCases.placeShot(token, gameId, coordinate) // does the action
                _game.value = useCases.getGame(token, gameId) ?: _game.value // updates the game
                while (true) {
                    delay(1000)
                    val gameFromServices = useCases.getGame(token, gameId) ?: throw Exception("Game not found")
                    userId.value ?: throw Exception("User not found")
                    if (gameFromServices.playerTurn == userId.value) {
                        _game.value = gameFromServices
                        break
                    }
                }
            }
        }
         */
    }

    private fun getMyBoard(game: Game) = if (myBoardDisplayed.value) game.board1 else game.board2
}