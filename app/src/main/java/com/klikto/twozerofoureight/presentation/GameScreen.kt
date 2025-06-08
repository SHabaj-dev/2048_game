package com.klikto.twozerofoureight.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klikto.twozerofoureight.R
import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.domain.model.Direction
import com.klikto.twozerofoureight.ui.components.dialogs.GameOverDialog
import com.klikto.twozerofoureight.ui.theme.Dimensions
import kotlin.math.abs
import kotlin.math.min

/**
 * Main game screen that displays the game board and controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    boardState: BoardState,
    highScore: Long,
    canUndo: Boolean,
    canRedo: Boolean,
    isGameOver: Boolean,
    onSwipe: (Direction) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragStartX by remember { mutableStateOf(0f) }
    var dragStartY by remember { mutableStateOf(0f) }
    var dragEndX by remember { mutableStateOf(0f) }
    var dragEndY by remember { mutableStateOf(0f) }
    var previousScore by remember { mutableStateOf(boardState.score) }
    val scoreAnimation = remember { Animatable(0f) }

    LaunchedEffect(boardState.score) {
        if (boardState.score > previousScore) {
            scoreAnimation.snapTo(1f)
            scoreAnimation.animateTo(0f, animationSpec = tween(500))
        }
        previousScore = boardState.score
    }

    if (isGameOver) {
        GameOverDialog(
            boardState = boardState,
            onRestart = onRestart,
            onQuit = onQuit
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Score: ${boardState.score}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.scale(1f + scoreAnimation.value * 0.2f)
                        )
                        Text(
                            text = "Best: $highScore",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onQuit) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Quit")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_undo),
                        contentDescription = "Undo"
                    )
                }
                IconButton(
                    onClick = onRedo,
                    enabled = canRedo
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_redo),
                        contentDescription = "Redo"
                    )
                }
                IconButton(onClick = onRestart) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragStartX = offset.x
                            dragStartY = offset.y
                            dragEndX = offset.x
                            dragEndY = offset.y
                        },
                        onDrag = { change, _ ->
                            dragEndX = change.position.x
                            dragEndY = change.position.y
                        },
                        onDragEnd = {
                            val dx = dragEndX - dragStartX
                            val dy = dragEndY - dragStartY
                            when {
                                abs(dx) > abs(dy) && abs(dx) > 50 -> {
                                    if (dx > 0) onSwipe(Direction.RIGHT)
                                    else onSwipe(Direction.LEFT)
                                }
                                abs(dy) > abs(dx) && abs(dy) > 50 -> {
                                    if (dy > 0) onSwipe(Direction.DOWN)
                                    else onSwipe(Direction.UP)
                                }
                            }
                        }
                    )
                }
        ) {
            GameBoard(
                boardState = boardState,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun GameBoard(
    boardState: BoardState,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val availableWidth = screenWidth - 32.dp // Account for padding
    val availableHeight = screenHeight - 200.dp // Account for top and bottom bars

    // Calculate cell size based on the smaller dimension and grid size
    val cellSize = with(LocalDensity.current) {
        val gridSize = boardState.size.toFloat()
        val spacingPx = 8.dp.toPx()
        val widthInPx = availableWidth.toPx() - (gridSize - 1f) * spacingPx
        val heightInPx = availableHeight.toPx() - (gridSize - 1f) * spacingPx
        val minDimension = min(widthInPx, heightInPx) / gridSize
        minDimension.toDp()
    }
    val spacing = 8.dp

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (row in 0 until boardState.size) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                for (col in 0 until boardState.size) {
                    val value = boardState.tiles[row * boardState.size + col]
                    val key = "${row}_${col}_${value}"
                    AnimatedGameCell(
                        value = value,
                        modifier = Modifier.size(cellSize),
                        key = key
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedGameCell(
    value: Int,
    modifier: Modifier = Modifier,
    key: String
) {
    var isNew by remember { mutableStateOf(true) }
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(key) {
        if (isNew) {
            scale.snapTo(0f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            isNew = false
        }
    }

    GameCell(
        value = value,
        modifier = modifier.scale(scale.value)
    )
}

@Composable
private fun GameCell(
    value: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (value) {
        0 -> Color(0xFFCDC1B4) // Empty cell
        2 -> Color(0xFFEEE4DA)
        4 -> Color(0xFFEDE0C8)
        8 -> Color(0xFFF2B179)
        16 -> Color(0xFFF59563)
        32 -> Color(0xFFF67C5F)
        64 -> Color(0xFFF65E3B)
        128 -> Color(0xFFEDCF72)
        256 -> Color(0xFFEDCC61)
        512 -> Color(0xFFEDC850)
        1024 -> Color(0xFFEDC53F)
        2048 -> Color(0xFFEDC22E)
        else -> Color(0xFF3C3A32) // For tiles >= 4096
    }

    val textColor = if (value <= 4) Color.Black else Color.White
    val fontSize = when {
        value >= 1000 -> 24.sp
        value >= 100 -> 28.sp
        else -> 32.sp
    }

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = fontSize,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
} 