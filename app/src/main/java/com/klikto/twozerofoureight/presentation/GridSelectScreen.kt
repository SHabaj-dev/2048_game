package com.klikto.twozerofoureight.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Screen for selecting the grid size before starting a new game.
 */
@Composable
fun GridSelectScreen(
    onGridSizeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "2048",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "Select Grid Size",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        GridSizeButton(
            text = "4 × 4",
            onClick = { onGridSizeSelected(4) },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        GridSizeButton(
            text = "6 × 6",
            onClick = { onGridSizeSelected(6) },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        GridSizeButton(
            text = "8 × 8",
            onClick = { onGridSizeSelected(8) },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun GridSizeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
} 