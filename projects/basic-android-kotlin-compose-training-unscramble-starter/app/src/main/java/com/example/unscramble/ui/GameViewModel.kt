package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState :StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        resetGame()
    }

    fun resetGame() {
        _uiState.value = GameUiState(
            currentScrambleWord = pickRandomWordAndShuffle(),
            usedWords = setOf(),
        )
    }

    fun updateUserGuess(guessWord: String) {
        _uiState.update { currentState ->
            currentState.copy(
                userGuess = guessWord,
            )
        }
    }

    fun checkUserGuess() {
        val userGuess = _uiState.value.userGuess
        val currentWord = _uiState.value.currentWord
        if (userGuess.equals(currentWord, ignoreCase = true)) {

        } else {
            _uiState.update { state ->
                state.copy(
                    isGuessedWordWrong = true,
                )
            }
        }
        updateUserGuess("")
    }

    private fun pickRandomWordAndShuffle(): String {
        val currentWord = allWords.random()
        val usedWords = _uiState.value.usedWords.toMutableSet()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            _uiState.update {currentState ->
                currentState.copy(
                    currentWord = currentWord,
                    usedWords = usedWords.apply {
                        add(currentWord)
                    }
                )
            }
            return currentWord
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val temp = word.toCharArray()
        temp.shuffle()
        while(String(temp).equals(word)){
            temp.shuffle()
        }
            return  String(temp)
    }
}