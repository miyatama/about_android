package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState :StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    private var userGuess by mutableStateOf("")

    init {
        resetGame()
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(
            currentScrambleWord = pickRandomWordAndShuffle(),
        )
    }

    fun updateUserGuess(guessWord: String) {
        _uiState.value = _uiState.value.copy(
            userGuess = guessWord,
        )
    }

    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
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