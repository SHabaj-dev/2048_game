package com.klikto.twozerofoureight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.activity.OnBackPressedCallback
import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.presentation.GameScreen
import com.klikto.twozerofoureight.presentation.GameViewModel
import com.klikto.twozerofoureight.presentation.ModeSelectScreen
import com.klikto.twozerofoureight.presentation.SplashScreen
import com.klikto.twozerofoureight.ui.theme.TwoZeroFourEightTheme

sealed class Screen {
    object Splash : Screen()
    object ModeSelect : Screen()
    object Game : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwoZeroFourEightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                    var showExitDialog by remember { mutableStateOf(false) }
                    val boardState by gameViewModel.boardState.collectAsState()
                    val highScore by gameViewModel.highScore.collectAsState()
                    val canUndo by gameViewModel.canUndo.collectAsState()
                    val canRedo by gameViewModel.canRedo.collectAsState()
                    val isGameOver by gameViewModel.isGameOver.collectAsState()

                    // Handle back press
                    DisposableEffect(Unit) {
                        val callback = object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                if (currentScreen == Screen.Game) {
                                    showExitDialog = true
                                } else {
                                    finish()
                                }
                            }
                        }
                        onBackPressedDispatcher.addCallback(callback)
                        onDispose {
                            callback.remove()
                        }
                    }

                    // Exit Confirmation Dialog
                    if (showExitDialog) {
                        AlertDialog(
                            onDismissRequest = { showExitDialog = false },
                            title = { Text("Exit Game") },
                            text = { Text("Are you sure you want to exit the game?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showExitDialog = false
                                        currentScreen = Screen.ModeSelect
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50) // Green
                                    )
                                ) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showExitDialog = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE57373) // Light Red
                                    )
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    when (currentScreen) {
                        Screen.Splash -> {
                            SplashScreen(
                                onSplashComplete = {
                                    currentScreen = Screen.ModeSelect
                                }
                            )
                        }
                        Screen.ModeSelect -> {
                            ModeSelectScreen(
                                onModeSelected = { size ->
                                    gameViewModel.initializeGame(size)
                                    currentScreen = Screen.Game
                                }
                            )
                        }
                        Screen.Game -> {
                            GameScreen(
                                boardState = boardState ?: BoardState(4, IntArray(16), 0),
                                highScore = highScore,
                                canUndo = canUndo,
                                canRedo = canRedo,
                                isGameOver = isGameOver,
                                onSwipe = { direction -> gameViewModel.swipe(direction) },
                                onUndo = { gameViewModel.undo() },
                                onRedo = { gameViewModel.redo() },
                                onRestart = { gameViewModel.restart() },
                                onQuit = { currentScreen = Screen.ModeSelect }
                            )
                        }
                    }
                }
            }
        }
    }
}