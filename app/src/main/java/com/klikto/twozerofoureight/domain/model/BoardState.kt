package com.klikto.twozerofoureight.domain.model

/**
 * Represents the current state of the game board.
 *
 * @property size The size of the board (e.g., 4 for a 4x4 board)
 * @property tiles Array representing the board where each element is the value of a tile (0 for empty)
 * @property score The current score of the game
 * @property isGameOver Whether the game is over (no more moves possible)
 * @property hasWon Whether the player has reached 2048
 */
data class BoardState(
    val size: Int,
    val tiles: IntArray,
    val score: Long,
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardState

        if (size != other.size) return false
        if (!tiles.contentEquals(other.tiles)) return false
        if (score != other.score) return false
        if (isGameOver != other.isGameOver) return false
        if (hasWon != other.hasWon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + tiles.contentHashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + isGameOver.hashCode()
        result = 31 * result + hasWon.hashCode()
        return result
    }
} 