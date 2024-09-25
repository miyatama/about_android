package com.example.unscramble.ui

data class GameUiState (
    val currentScrambleWord: String = "",
    val currentWord: String = "",
    val userGuess: String = "",
    val usedWords: Set<String> = setOf(),
    val isGuessedWordWrong: Boolean = false,
)