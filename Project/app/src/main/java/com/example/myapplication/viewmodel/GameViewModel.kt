package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.DataStoreManager
import com.example.myapplication.data.model.Score
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameState(
    val playerName: String = "",
    val score: Int = 0,
    val timeLeft: Int = 60,
    val grid: List<Boolean> = List(12) { false },
    val isGameOver: Boolean = false,
    val targetColorHex: String = "#E63946"
)

sealed class GameIntent {
    data class StartGame(val playerName: String, val targetColorHex: String) : GameIntent()
    data class ClickTarget(val index: Int) : GameIntent()
    object PlayAgain : GameIntent()
}

class GameViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun processIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartGame -> startGame(intent.playerName, intent.targetColorHex)
            is GameIntent.ClickTarget -> handleTargetClick(intent.index)
            is GameIntent.PlayAgain -> startGame(_state.value.playerName, _state.value.targetColorHex)
        }
    }

    private fun startGame(playerName: String, targetColorHex: String) {
        timerJob?.cancel()
        _state.update {
            GameState(
                playerName = playerName,
                score = 0,
                timeLeft = 60,
                grid = generateInitialGrid(),
                isGameOver = false,
                targetColorHex = targetColorHex
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.timeLeft > 0) {
                delay(1000)
                _state.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            endGame()
        }
    }

    private fun handleTargetClick(index: Int) {
        if (_state.value.isGameOver || _state.value.timeLeft <= 0) return

        val currentGrid = _state.value.grid.toMutableList()
        if (currentGrid[index]) {
            // Target correctly clicked
            currentGrid[index] = false // Remove target
            spawnNewTarget(currentGrid)
            
            _state.update {
                it.copy(
                    score = it.score + 10,
                    grid = currentGrid
                )
            }
        }
    }

    private fun spawnNewTarget(grid: MutableList<Boolean>) {
        val emptyIndices = grid.mapIndexedNotNull { i, isTarget -> if (!isTarget) i else null }
        if (emptyIndices.isNotEmpty()) {
            val randomIndex = emptyIndices[Random.nextInt(emptyIndices.size)]
            grid[randomIndex] = true
        }
    }

    private fun generateInitialGrid(): List<Boolean> {
        // 12 slots, 3 of them are true
        val grid = MutableList(12) { false }
        var spawned = 0
        while (spawned < 3) {
            val idx = Random.nextInt(12)
            if (!grid[idx]) {
                grid[idx] = true
                spawned++
            }
        }
        return grid
    }

    private fun endGame() {
        timerJob?.cancel()
        _state.update { it.copy(isGameOver = true) }
        // Save score
        val finalState = _state.value
        viewModelScope.launch {
            val scoreRecord = Score(
                playerName = finalState.playerName.ifEmpty { "UNKNOWN" },
                score = finalState.score
            )
            dataStoreManager.saveScore(scoreRecord)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
