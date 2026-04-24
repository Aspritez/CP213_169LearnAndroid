package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.DataStoreManager
import com.example.myapplication.data.model.GameMode
import com.example.myapplication.data.model.Score
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ===== GameState =====
 * สถานะของเกม Gridshot ทั้งหมด
 *
 * เกี่ยวข้องกับ:
 *   - GameScreen → อ่าน state เพื่อแสดง UI (grid, score, timer, countdown, game over)
 *   - GameViewModel → จัดการ/อัปเดต state ตาม game logic
 *
 * Fields:
 *   - countdown: ค่านับถอยหลัง 3→2→1→0 (0 = เริ่มเกม)
 *   - isCountingDown: true ระหว่างนับถอยหลัง → ปิดการกดเป้า
 *   - grid: List<Boolean> ขนาด 12 ช่อง (3x4), true = มีเป้า
 *   - lastClickedIndex: index ล่าสุดที่ถูกคลิก → ป้องกันเป้าซ้ำตำแหน่ง
 *   - backgroundColorHex: สีพื้นหลังหน้าเกม (จาก Settings)
 */
data class GameState(
    val playerName: String = "",
    val score: Int = 0,
    val timeLeft: Int = 60,
    val grid: List<Boolean> = List(12) { false },
    val isGameOver: Boolean = false,
    val targetColorHex: String = "#E63946",
    val countdown: Int = 3,             // นับถอยหลัง 3,2,1 ก่อนเริ่มเกม
    val isCountingDown: Boolean = true, // true = กำลังนับถอยหลัง, false = เกมเริ่มแล้ว
    val lastClickedIndex: Int = -1,     // index ล่าสุดที่ถูกคลิก → ป้องกันซ้ำตำแหน่ง
    val backgroundColorHex: String = "#0A192F"  // สีพื้นหลังหน้าเกม
)

/**
 * ===== GameIntent =====
 * Intent pattern สำหรับส่ง action เข้า ViewModel
 * ใช้ MVI pattern: UI ส่ง Intent → ViewModel ประมวลผล → อัปเดต State → UI แสดงผล
 */
sealed class GameIntent {
    data class StartGame(
        val playerName: String,
        val targetColorHex: String,
        val backgroundColorHex: String = "#0A192F"
    ) : GameIntent()
    data class ClickTarget(val index: Int) : GameIntent()
    object PlayAgain : GameIntent()
}

/**
 * ===== GameViewModel =====
 * จัดการ logic เกมโหมด Gridshot
 *
 * เกี่ยวข้องกับ:
 *   - DataStoreManager → บันทึกคะแนนหลังจบเกม
 *   - GameScreen → อ่าน state, ส่ง intent (click, play again)
 *   - MainActivity → สร้าง instance ด้วย Factory, ส่ง StartGame intent
 *   - AudioController → ถูกเรียกจาก GameScreen (ไม่ผ่าน ViewModel)
 *
 * Flow:
 *   StartGame → Countdown 3→2→1 → startTimer (60s) → เล่น → endGame → saveScore
 */
class GameViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var countdownJob: Job? = null

    /**
     * ===== Process Intent =====
     * จุดรับ intent จาก UI แล้วส่งต่อไปยัง function ที่เหมาะสม
     */
    fun processIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.StartGame -> startGame(intent.playerName, intent.targetColorHex, intent.backgroundColorHex)
            is GameIntent.ClickTarget -> handleTargetClick(intent.index)
            is GameIntent.PlayAgain -> startGame(
                _state.value.playerName,
                _state.value.targetColorHex,
                _state.value.backgroundColorHex
            )
        }
    }

    /**
     * ===== Start Game =====
     * เริ่มเกมใหม่ — reset state ทั้งหมด แล้วเริ่ม countdown
     * Flow: reset state → countdown 3→2→1 → startTimer()
     */
    private fun startGame(playerName: String, targetColorHex: String, backgroundColorHex: String) {
        timerJob?.cancel()
        countdownJob?.cancel()

        // ===== ป้องกันสีเป้าซ้ำกับพื้นหลัง =====
        val safeTargetColor = ensureSafeTargetColor(targetColorHex, backgroundColorHex)

        _state.update {
            GameState(
                playerName = playerName,
                score = 0,
                timeLeft = 60,
                grid = generateInitialGrid(),
                isGameOver = false,
                targetColorHex = safeTargetColor,
                countdown = 3,
                isCountingDown = true,
                lastClickedIndex = -1,
                backgroundColorHex = backgroundColorHex
            )
        }
        startCountdown()
    }

    /**
     * ===== Countdown =====
     * นับถอยหลัง 3→2→1 ก่อนเริ่มเกมจริง
     * ระหว่าง countdown: ผู้เล่นจะกดเป้าไม่ได้ (isCountingDown = true)
     * หลัง countdown: isCountingDown = false → startTimer()
     */
    private fun startCountdown() {
        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _state.update { it.copy(countdown = i) }
                delay(1000)
            }
            // Countdown จบ → เริ่มเกมจริง
            _state.update { it.copy(countdown = 0, isCountingDown = false) }
            startTimer()
        }
    }

    /**
     * ===== Timer =====
     * นับเวลาถอยหลัง 60 วินาที — เริ่มหลัง countdown จบ
     * เมื่อครบเวลา → endGame()
     */
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.timeLeft > 0) {
                delay(1000)
                _state.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            endGame()
        }
    }

    /**
     * ===== Handle Target Click =====
     * เมื่อผู้เล่นกดเป้า:
     * - ตรวจสอบว่าเกมยังไม่จบ และไม่อยู่ระหว่าง countdown
     * - ถ้ากดถูกเป้า: ลบเป้าเดิม, spawn เป้าใหม่ (ไม่ซ้ำตำแหน่งเดิม), +10 คะแนน
     */
    private fun handleTargetClick(index: Int) {
        if (_state.value.isGameOver || _state.value.isCountingDown || _state.value.timeLeft <= 0) return

        val currentGrid = _state.value.grid.toMutableList()
        if (currentGrid[index]) {
            // เป้าถูกกด → ลบเป้าเดิม + spawn ใหม่ (ไม่ซ้ำตำแหน่ง index ที่เพิ่งกด)
            currentGrid[index] = false
            spawnNewTarget(currentGrid, excludeIndex = index)

            _state.update {
                it.copy(
                    score = it.score + 10,
                    grid = currentGrid,
                    lastClickedIndex = index
                )
            }
        }
    }

    /**
     * ===== Spawn New Target =====
     * สร้างเป้าใหม่ในตำแหน่ง random ที่ว่างอยู่
     * ป้องกันไม่ให้เป้าขึ้นที่ตำแหน่งเดิมที่เพิ่งถูกคลิก (excludeIndex)
     */
    private fun spawnNewTarget(grid: MutableList<Boolean>, excludeIndex: Int = -1) {
        val emptyIndices = grid.mapIndexedNotNull { i, isTarget ->
            if (!isTarget && i != excludeIndex) i else null
        }
        if (emptyIndices.isNotEmpty()) {
            val randomIndex = emptyIndices[Random.nextInt(emptyIndices.size)]
            grid[randomIndex] = true
        } else {
            // fallback: ถ้าไม่มีช่องว่างอื่น → ใช้ช่องว่างทั้งหมดรวม excludeIndex
            val allEmpty = grid.mapIndexedNotNull { i, isTarget -> if (!isTarget) i else null }
            if (allEmpty.isNotEmpty()) {
                grid[allEmpty[Random.nextInt(allEmpty.size)]] = true
            }
        }
    }

    /**
     * ===== Generate Initial Grid =====
     * สร้าง grid เริ่มต้น 12 ช่อง โดยมีเป้า 3 ตัวในตำแหน่ง random
     */
    private fun generateInitialGrid(): List<Boolean> {
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

    /**
     * ===== End Game =====
     * จบเกม → หยุด timer, บันทึกคะแนนลง DataStore
     * Score จะมี gameMode = GRIDSHOT เพื่อแยก leaderboard
     */
    private fun endGame() {
        timerJob?.cancel()
        _state.update { it.copy(isGameOver = true) }
        val finalState = _state.value
        viewModelScope.launch {
            val scoreRecord = Score(
                playerName = finalState.playerName.ifEmpty { "UNKNOWN" },
                score = finalState.score,
                gameMode = GameMode.GRIDSHOT.name,  // ← บันทึกว่าเป็นโหมด Gridshot
                accuracy = 100f   // Gridshot ไม่มี miss → accuracy 100%
            )
            dataStoreManager.saveScore(scoreRecord)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()
    }
}

/**
 * ===== ensureSafeTargetColor =====
 * ตรวจสอบว่าสีเป้ากับสีพื้นหลังคล้ายกันเกินไปหรือไม่
 * ถ้าคล้ายกัน (Euclidean distance < threshold) → ใช้สีขาว (#FFFFFF) แทน
 * ป้องกัน bug ที่ผู้เล่นมองไม่เห็นเป้า
 */
fun ensureSafeTargetColor(targetHex: String, backgroundHex: String): String {
    return try {
        if (isColorTooSimilar(targetHex, backgroundHex)) {
            // สีเป้าคล้ายพื้นหลังเกินไป → ใช้สีขาวถ้าพื้นหลังเข้ม หรือสีแดงถ้าพื้นหลังสว่าง
            val bgColor = android.graphics.Color.parseColor(backgroundHex)
            val bgLuminance = (android.graphics.Color.red(bgColor) * 0.299 +
                    android.graphics.Color.green(bgColor) * 0.587 +
                    android.graphics.Color.blue(bgColor) * 0.114)
            if (bgLuminance < 128) "#FFFFFF" else "#E63946"
        } else {
            targetHex
        }
    } catch (e: Exception) {
        "#E63946" // fallback สีแดงเริ่มต้น
    }
}

/**
 * ===== isColorTooSimilar =====
 * เปรียบเทียบสี 2 สีด้วย Euclidean distance ของ RGB
 * ถ้าระยะห่าง < threshold (default 80) → ถือว่าคล้ายกันเกินไป
 */
fun isColorTooSimilar(hex1: String, hex2: String, threshold: Int = 80): Boolean {
    return try {
        val c1 = android.graphics.Color.parseColor(hex1)
        val c2 = android.graphics.Color.parseColor(hex2)
        val dr = android.graphics.Color.red(c1) - android.graphics.Color.red(c2)
        val dg = android.graphics.Color.green(c1) - android.graphics.Color.green(c2)
        val db = android.graphics.Color.blue(c1) - android.graphics.Color.blue(c2)
        Math.sqrt((dr * dr + dg * dg + db * db).toDouble()) < threshold
    } catch (e: Exception) {
        false
    }
}
