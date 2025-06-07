package com.klikto.twozerofoureight.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.klikto.twozerofoureight.domain.GameEngine
import com.klikto.twozerofoureight.domain.GameEngineImpl
import com.klikto.twozerofoureight.domain.model.BoardState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_state")

/**
 * Repository for managing game state and high scores.
 */
class GameStateRepository(private val context: Context) {
    private val dataStore = context.dataStore

    /**
     * Creates a new game engine with the specified board size.
     */
    fun createGameEngine(boardSize: Int): GameEngine {
        return GameEngineImpl(boardSize)
    }

    /**
     * Gets the high score for a specific board size.
     */
    fun getHighScore(boardSize: Int): Flow<Long> {
        val key = longPreferencesKey("high_score_${boardSize}x${boardSize}")
        return dataStore.data.map { preferences ->
            preferences[key] ?: 0L
        }
    }

    /**
     * Updates the high score for a specific board size if the new score is higher.
     */
    suspend fun updateHighScore(boardSize: Int, score: Long) {
        val key = longPreferencesKey("high_score_${boardSize}x${boardSize}")
        dataStore.edit { preferences ->
            val currentHighScore = preferences[key] ?: 0L
            if (score > currentHighScore) {
                preferences[key] = score
            }
        }
    }
} 