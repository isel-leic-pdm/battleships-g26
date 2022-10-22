package com.example.battleships.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.BattlePhase
import com.example.battleships.game.services.BattleshipsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val gameService: BattleshipsService,
    private val token: String
) : ViewModel() {
    private val _userId: MutableState<Int?> = mutableStateOf(null)
    val userId: State<Int?>
        get() = _userId
    private val _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game
    private val _myBoardDisplayed: MutableState<Boolean> = mutableStateOf(true)
    val myBoardDisplayed: State<Boolean>
        get() = _myBoardDisplayed

    fun startGame() {
        viewModelScope.launch {
            gameService.startNewGame(token)
            _userId.value = gameService.getUserId(token)
            _game.value = gameService.getGameState(token)
        }
    }

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryPlaceShip(shipType, coordinate, orientation)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.placeShip(token, shipType, coordinate, orientation)
                    _game.value = gameService.getGameState(token)
                }
            }
        }
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryMoveShip(origin, destination)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.moveShip(token, origin, destination)
                    _game.value = gameService.getGameState(token)
                }
            }
        }
    }

    fun rotateShip(position: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryRotateShip(position)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.rotateShip(token, position)
                    _game.value = gameService.getGameState(token) ?: _game.value
                }
            }
        }
    }

    fun confirmFleet() {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.confirmFleet()
                viewModelScope.launch {
                    gameService.confirmFleet(token)
                    _game.value = gameService.getGameState(token) ?: _game.value
                }
            }
        }
    }

    fun placeShot(coordinate: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is BattlePhase) {
            gameInternal.tryPlaceShot(gameInternal.player1, coordinate) // tests if the action is valid
                ?: return
            viewModelScope.launch {
                gameService.placeShot(token, coordinate) // does the action
                _game.value = gameService.getGameState(token) ?: _game.value // updates the game
                while (true) {
                    delay(1000)
                    val gameFromServices = gameService.getGameState(token) ?: throw Exception("Game not found")
                    gameFromServices as? BattlePhase ?: throw Exception("Game is not in battle phase")
                    userId.value ?: throw Exception("User not found")
                    if (gameFromServices.playersTurn == userId.value) {
                        _game.value = gameFromServices
                        break
                    }
                }
            }
        }
    }
}