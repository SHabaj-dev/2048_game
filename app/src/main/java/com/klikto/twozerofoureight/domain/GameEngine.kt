package com.klikto.twozerofoureight.domain

import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.domain.model.Direction

/**
 * Core game engine interface that handles all game logic operations.
 * This interface is platform-independent and contains pure game logic.
 */
interface GameEngine {
    /**
     * Performs a swipe in the specified direction and returns the new board state.
     * If the move is invalid (no tiles can move), returns the current state.
     *
     * @param direction The direction to swipe
     * @return The new board state after the move
     */
    fun swipe(direction: Direction): BoardState

    /**
     * Undoes the last move and returns the previous board state.
     * If there are no moves to undo, returns the current state.
     *
     * @return The previous board state
     */
    fun undo(): BoardState

    /**
     * Redoes the last undone move and returns the new board state.
     * If there are no moves to redo, returns the current state.
     *
     * @return The new board state after redo
     */
    fun redo(): BoardState

    /**
     * Restarts the game with the current board size.
     *
     * @return A new board state with two initial tiles
     */
    fun restart(): BoardState

    /**
     * Gets the current board state.
     *
     * @return The current board state
     */
    fun getCurrentState(): BoardState

    /**
     * Checks if undo is available.
     *
     * @return true if there are moves to undo
     */
    fun canUndo(): Boolean

    /**
     * Checks if redo is available.
     *
     * @return true if there are moves to redo
     */
    fun canRedo(): Boolean

    /**
     * Checks if there are any valid moves left on the board.
     */
    fun hasValidMoves(state: BoardState): Boolean
}