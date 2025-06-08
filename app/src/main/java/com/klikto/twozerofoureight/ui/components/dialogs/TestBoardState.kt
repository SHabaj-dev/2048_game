package com.klikto.twozerofoureight.ui.components.dialogs

import com.klikto.twozerofoureight.domain.model.BoardState

val TestBoardState = BoardState(
    size = 4,
    tiles = IntArray(16) { 0 },
    score = 1234,
    isGameOver = false,
    hasWon = false
)
