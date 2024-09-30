package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
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
        val word = pickRandomWordAndShuffle()
        _uiState.value = GameUiState(
            currentWord = word,
            currentScrambleWord = shuffleCurrentWord(word),
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
            updateGameScore(
                _uiState.value.score.plus(SCORE_INCREASE)
            )
        } else {
            _uiState.update { state ->
                state.copy(
                    isGuessedWordWrong = true,
                )
            }
        }
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameScore(_uiState.value.score)
        updateUserGuess("")
    }

    private fun pickRandomWordAndShuffle(): String {
        val currentWord = allWords.random()
        val usedWords = _uiState.value.usedWords.toMutableSet()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            return currentWord
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val temp = word.toCharArray()
        temp.shuffle()
        while(String(temp).equals(word)){
            temp.shuffle()
        }
        return String(temp)
    }

    private fun updateGameScore(newScore: Int) {
        if (_uiState.value.usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { state ->
                state.copy(
                    isGameOver = true,
                    score = newScore,
                    isGuessedWordWrong = false,
                )
            }
        } else {
            val word = pickRandomWordAndShuffle()
            _uiState.update { state ->
                state.copy(
                    score = newScore,
                    currentWord = word,
                    currentScrambleWord = shuffleCurrentWord(word),
                    currentWordCount = state.currentWordCount.inc(),
                    isGuessedWordWrong = false,
                    usedWords = state.usedWords.toMutableSet().apply {
                        add(word)
                    }
                )
            }
        }
    }
}