package com.klikto.twozerofoureight.ui.components.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
// Removed icon imports since we're using text instead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.tooling.preview.Preview
import com.klikto.twozerofoureight.domain.model.BoardState
import com.klikto.twozerofoureight.ui.theme.TwoZeroFourEightTheme
import com.klikto.twozerofoureight.ui.components.dialogs.TestBoardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverDialog(
    boardState: BoardState,
    onRestart: () -> Unit,
    onQuit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = { /* Don't dismiss */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game Over Title with animation
                val titleScale = remember { Animatable(1f) }
                LaunchedEffect(Unit) {
                    titleScale.animateTo(
                        targetValue = 1.2f,
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    )
                }
                Text(
                    text = "Game Over",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .scale(titleScale.value)
                )

                // Score with animation
                val scoreScale = remember { Animatable(1f) }
                LaunchedEffect(Unit) {
                    scoreScale.animateTo(
                        targetValue = 1.1f,
                        animationSpec = tween(500, easing = FastOutSlowInEasing)
                    )
                }
                Text(
                    text = "Your Score: ${boardState.score}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .scale(scoreScale.value)
                )

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GameOverActionButton(
                        text = "Restart",
                        onClick = onRestart,
                        modifier = Modifier.weight(1f)
                    )
                    GameOverActionButton(
                        text = "Home",
                        onClick = onQuit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverDialogPreview() {
    TwoZeroFourEightTheme {
        GameOverDialog(
            boardState = TestBoardState,
            onRestart = {},
            onQuit = {}
        )
    }
}
