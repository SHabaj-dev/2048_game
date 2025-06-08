package com.klikto.twozerofoureight.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.klikto.twozerofoureight.data.GameStateRepository
import com.klikto.twozerofoureight.domain.GameEngine
import com.klikto.twozerofoureight.domain.GameEngineImpl
import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.domain.model.Direction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the game state and user interactions.
 */
class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository = GameStateRepository(application)
    private var gameEngine: GameEngine? = null
    private var currentBoardSize: Int = 4

    private val _boardState = MutableStateFlow<BoardState?>(null)
    val boardState: StateFlow<BoardState?> = _boardState.asStateFlow()

    private val _highScore = MutableStateFlow(0L)
    val highScore: StateFlow<Long> = _highScore.asStateFlow()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver.asStateFlow()

    private fun checkGameOver() {
        val currentState = _boardState.value ?: return
        val hasValidMoves = gameEngine?.hasValidMoves(currentState) ?: true
        _isGameOver.value = !hasValidMoves
    }

    init {
        // Restore board size from saved state or default to 4
        val boardSize = savedStateHandle.get<Int>("board_size") ?: 4
        initializeGame(boardSize)
    }

    /**
     * Initializes the game with the specified board size.
     */
    fun initializeGame(size: Int) {
        currentBoardSize = size
        savedStateHandle["board_size"] = size
        gameEngine = GameEngineImpl(size)
        _boardState.value = gameEngine?.getCurrentState()
        updateUndoRedoState()
        viewModelScope.launch {
            repository.getHighScore(size).collect { score ->
                _highScore.value = score
            }
        }
    }

    /**
     * Performs a swipe in the specified direction.
     */
    fun swipe(direction: Direction) {
        gameEngine?.let { engine ->
            _boardState.value = engine.swipe(direction)
            updateUndoRedoState()
            checkHighScore()
            checkGameOver()
        }
    }

    /**
     * Undoes the last move.
     */
    fun undo() {
        gameEngine?.let { engine ->
            _boardState.value = engine.undo()
            updateUndoRedoState()
        }
    }

    /**
     * Redoes the last undone move.
     */
    fun redo() {
        gameEngine?.let { engine ->
            _boardState.value = engine.redo()
            updateUndoRedoState()
        }
    }

    /**
     * Restarts the current game.
     */
    fun restart() {
        gameEngine?.let { engine ->
            _boardState.value = engine.restart()
            updateUndoRedoState()
            _isGameOver.value = false
        }
    }

    private fun updateUndoRedoState() {
        gameEngine?.let { engine ->
            _canUndo.value = engine.canUndo()
            _canRedo.value = engine.canRedo()
        }
    }

    private fun checkHighScore() {
        _boardState.value?.let { state ->
            if (state.score > _highScore.value) {
                viewModelScope.launch {
                    repository.updateHighScore(currentBoardSize, state.score)
                }
            }
        }
    }
} 