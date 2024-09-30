package com.example.unscramble.ui

data class GameUiState (
    val currentWord: String = "",
    val currentScrambleWord: String = "",
    val currentWordCount: Int = 1,
    val userGuess: String = "",
    val usedWords: Set<String> = setOf(),
    val isGuessedWordWrong: Boolean = false,
    val score: Int = 0,
    val isGameOver: Boolean = false,
)