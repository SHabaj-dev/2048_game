package com.klikto.twozerofoureight.domain

import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.domain.model.Direction
import kotlin.random.Random

/**
 * Implementation of the GameEngine interface that handles the core game logic.
 */
class GameEngineImpl(
    private val boardSize: Int,
    private val random: Random = Random.Default
) : GameEngine {
    private val maxHistorySize = 50
    private val undoStack = mutableListOf<BoardState>()
    private val redoStack = mutableListOf<BoardState>()
    private var currentState: BoardState = createInitialState()

    override fun swipe(direction: Direction): BoardState {
        val newState = performSwipe(currentState, direction)
        if (newState != currentState) {
            undoStack.add(currentState)
            if (undoStack.size > maxHistorySize) {
                undoStack.removeAt(0)
            }
            redoStack.clear()
            currentState = newState
        }
        return currentState
    }

    override fun undo(): BoardState {
        if (undoStack.isEmpty()) return currentState
        
        redoStack.add(currentState)
        currentState = undoStack.removeAt(undoStack.lastIndex)
        return currentState
    }

    override fun redo(): BoardState {
        if (redoStack.isEmpty()) return currentState
        
        undoStack.add(currentState)
        currentState = redoStack.removeAt(redoStack.lastIndex)
        return currentState
    }

    override fun restart(): BoardState {
        undoStack.clear()
        redoStack.clear()
        currentState = createInitialState()
        return currentState
    }

    override fun getCurrentState(): BoardState = currentState

    override fun canUndo(): Boolean = undoStack.isNotEmpty()

    override fun canRedo(): Boolean = redoStack.isNotEmpty()

    private fun createInitialState(): BoardState {
        val tiles = IntArray(boardSize * boardSize)
        addRandomTile(tiles)
        addRandomTile(tiles)
        return BoardState(boardSize, tiles, 0)
    }

    private fun performSwipe(state: BoardState, direction: Direction): BoardState {
        val newTiles = state.tiles.copyOf()
        var score = state.score
        var moved = false

        when (direction) {
            Direction.LEFT -> {
                for (row in 0 until state.size) {
                    val rowTiles = IntArray(state.size) { col -> newTiles[row * state.size + col] }
                    val (newRow, newScore, rowMoved) = mergeRow(rowTiles, 1)
                    score += newScore
                    moved = moved || rowMoved
                    for (col in 0 until state.size) {
                        newTiles[row * state.size + col] = newRow[col]
                    }
                }
            }
            Direction.RIGHT -> {
                for (row in 0 until state.size) {
                    val rowTiles = IntArray(state.size) { col -> newTiles[row * state.size + (state.size - 1 - col)] }
                    val (newRow, newScore, rowMoved) = mergeRow(rowTiles, 1)
                    score += newScore
                    moved = moved || rowMoved
                    for (col in 0 until state.size) {
                        newTiles[row * state.size + col] = newRow[state.size - 1 - col]
                    }
                }
            }
            Direction.UP -> {
                for (col in 0 until state.size) {
                    val colTiles = IntArray(state.size) { row -> newTiles[row * state.size + col] }
                    val (newCol, newScore, colMoved) = mergeRow(colTiles, 1)
                    score += newScore
                    moved = moved || colMoved
                    for (row in 0 until state.size) {
                        newTiles[row * state.size + col] = newCol[row]
                    }
                }
            }
            Direction.DOWN -> {
                for (col in 0 until state.size) {
                    val colTiles = IntArray(state.size) { row -> newTiles[(state.size - 1 - row) * state.size + col] }
                    val (newCol, newScore, colMoved) = mergeRow(colTiles, 1)
                    score += newScore
                    moved = moved || colMoved
                    for (row in 0 until state.size) {
                        newTiles[row * state.size + col] = newCol[state.size - 1 - row]
                    }
                }
            }
        }

        if (!moved) return state

        addRandomTile(newTiles)
        
        val tempState = BoardState(state.size, newTiles, score)
        val hasWon = newTiles.any { it >= 2048 }
        val isGameOver = !hasValidMoves(tempState)

        return BoardState(
            size = state.size,
            tiles = newTiles,
            score = score,
            isGameOver = isGameOver,
            hasWon = hasWon
        )
    }

    private fun mergeRow(row: IntArray, step: Int): Triple<IntArray, Int, Boolean> {
        val size = row.size
        var score = 0
        var moved = false

        // First pass: move all non-zero tiles to one side
        val temp = IntArray(size)
        var writeIndex = if (step > 0) 0 else size - 1
        var readIndex = if (step > 0) 0 else size - 1

        while (if (step > 0) readIndex < size else readIndex >= 0) {
            if (row[readIndex] != 0) {
                temp[writeIndex] = row[readIndex]
                if (writeIndex != readIndex) moved = true
                writeIndex += step
            }
            readIndex += step
        }

        // Second pass: merge adjacent tiles
        val merged = IntArray(size)
        writeIndex = if (step > 0) 0 else size - 1
        var i = if (step > 0) 0 else size - 1
        while (if (step > 0) i < size - 1 else i > 0) {
            if (temp[i] != 0 && temp[i] == temp[i + step]) {
                merged[writeIndex] = temp[i] * 2
                score += temp[i] * 2
                moved = true
                i += 2 * step
            } else {
                merged[writeIndex] = temp[i]
                i += step
            }
            writeIndex += step
        }
        
        // Handle the last element if it hasn't been processed
        if (if (step > 0) i < size else i >= 0) {
            merged[writeIndex] = temp[i]
        }

        return Triple(merged, score, moved)
    }

    private fun addRandomTile(tiles: IntArray) {
        val emptyCells = tiles.indices.filter { tiles[it] == 0 }
        if (emptyCells.isEmpty()) return

        val value = if (random.nextDouble() < 0.9) 2 else 4
        tiles[emptyCells[random.nextInt(emptyCells.size)]] = value
    }

    override fun hasValidMoves(state: BoardState): Boolean {
        val tiles = state.tiles
        val size = state.size
        
        // Check for empty cells
        if (tiles.any { it == 0 }) return true

        // Check for possible merges horizontally
        for (row in 0 until size) {
            for (col in 0 until size - 1) {
                if (tiles[row * size + col] == tiles[row * size + col + 1]) {
                    return true
                }
            }
        }

        // Check for possible merges vertically
        for (col in 0 until size) {
            for (row in 0 until size - 1) {
                if (tiles[row * size + col] == tiles[(row + 1) * size + col]) {
                    return true
                }
            }
        }

        return false
    }
} 