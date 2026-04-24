package com.example.myapplication.viewmodel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import kotlin.math.abs
import kotlin.random.Random

/**
 * ===== GyroscopeGameState =====
 * สถานะเกมโหมด Gyroscope Training
 *
 * เกี่ยวข้องกับ:
 *   - GyroscopeGameScreen → อ่าน state เพื่อแสดง UI
 *   - GyroscopeViewModel → จัดการ/อัปเดต state
 *
 * Fields สำคัญ:
 *   - crosshairX/Y: ตำแหน่ง crosshair (จุดเล็ง) เป็น fraction (0.0 - 1.0) ของพื้นที่เกม
 *   - targetX/Y: ตำแหน่งเป้าหมาย เป็น fraction (0.0 - 1.0) ของพื้นที่เกม
 *   - hitCount: จำนวนครั้งที่ยิงโดน
 *   - missCount: จำนวนครั้งที่ยิงพลาด
 *   - totalShots: จำนวนครั้งที่ยิงทั้งหมด (hitCount + missCount)
 *   - accuracy: ความแม่นยำ เป็น % (hitCount / totalShots * 100)
 *   - backgroundColorHex: สีพื้นหลังหน้าเกม (จาก Settings)
 */
data class GyroscopeGameState(
    val playerName: String = "",
    val score: Int = 0,
    val timeLeft: Int = 60,
    val isGameOver: Boolean = false,
    val targetColorHex: String = "#E63946",
    val countdown: Int = 3,
    val isCountingDown: Boolean = true,
    // ===== Crosshair (จุดเล็ง) — ควบคุมโดย gyroscope sensor =====
    val crosshairX: Float = 0.5f,   // ตำแหน่งแนวนอน (0.0 = ซ้าย, 1.0 = ขวา)
    val crosshairY: Float = 0.5f,   // ตำแหน่งแนวตั้ง (0.0 = บน, 1.0 = ล่าง)
    // ===== Target (เป้าหมาย) — อยู่นิ่ง, random ตำแหน่ง =====
    val targetX: Float = 0.5f,      // ตำแหน่งเป้าแนวนอน
    val targetY: Float = 0.5f,      // ตำแหน่งเป้าแนวตั้ง
    // ===== Accuracy Tracking =====
    val hitCount: Int = 0,
    val missCount: Int = 0,
    val totalShots: Int = 0,
    val accuracy: Float = 0f,       // ความแม่นยำ %
    // ===== Background Color =====
    val backgroundColorHex: String = "#0A192F"  // สีพื้นหลังหน้าเกม
)

/**
 * ===== GyroscopeIntent =====
 * Intent สำหรับ Gyroscope game — เหมือนกับ GameIntent แต่เพิ่ม Fire
 */
sealed class GyroscopeIntent {
    data class StartGame(
        val playerName: String,
        val targetColorHex: String,
        val sensitivity: Float,
        val backgroundColorHex: String = "#0A192F"
    ) : GyroscopeIntent()
    object Fire : GyroscopeIntent()     // กดปุ่มยิง
    object PlayAgain : GyroscopeIntent()
}

/**
 * ===== GyroscopeViewModel =====
 * จัดการ logic เกมโหมด Gyroscope Training
 *
 * เกี่ยวข้องกับ:
 *   - SensorManager → อ่านค่า gyroscope sensor แบบ real-time
 *   - DataStoreManager → บันทึกคะแนนหลังจบเกม
 *   - GyroscopeGameScreen → อ่าน state, ส่ง intent (fire, play again)
 *   - AppSettings.gyroSensitivity → กำหนดความไวของ crosshair
 *
 * การทำงาน:
 *   1. ผู้เล่นเอียงเครื่อง → gyroscope ส่งค่า rotation rate
 *   2. ViewModel แปลงค่า sensor เป็นตำแหน่ง crosshair
 *   3. ผู้เล่นกดปุ่มยิง → ตรวจสอบว่า crosshair อยู่ใกล้เป้าไหม
 *   4. ถ้าใกล้พอ = hit (+10 คะแนน, spawn เป้าใหม่)
 *   5. ถ้าไกล = miss (+1 missCount)
 *   6. คำนวณ accuracy = hitCount / totalShots * 100
 */
class GyroscopeViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel(), SensorEventListener {

    private val _state = MutableStateFlow(GyroscopeGameState())
    val state: StateFlow<GyroscopeGameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var countdownJob: Job? = null

    // ===== Sensor =====
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // ===== Sensitivity ===== 
    // ค่าที่กำหนดความเร็วของ crosshair ตามการเอียงเครื่อง
    // ค่าสูง = crosshair เคลื่อนที่เร็ว, ค่าต่ำ = crosshair เคลื่อนที่ช้า
    private var sensitivity: Float = 3.0f

    // ===== Hit Detection Threshold =====
    // ถ้า crosshair อยู่ห่างจากเป้าน้อยกว่า threshold = ยิงโดน
    private val hitThreshold = 0.08f // 8% ของพื้นที่เกม

    // ===== Minimum Distance for Anti-Repeat =====
    // เป้าใหม่ต้องห่างจากเป้าเดิมอย่างน้อย 20% ของพื้นที่เกม
    private val minTargetDistance = 0.2f

    /**
     * ===== Process Intent =====
     * จุดรับ intent จาก UI
     */
    fun processIntent(intent: GyroscopeIntent) {
        when (intent) {
            is GyroscopeIntent.StartGame -> startGame(
                intent.playerName, intent.targetColorHex, intent.sensitivity, intent.backgroundColorHex
            )
            is GyroscopeIntent.Fire -> handleFire()
            is GyroscopeIntent.PlayAgain -> startGame(
                _state.value.playerName,
                _state.value.targetColorHex,
                sensitivity,
                _state.value.backgroundColorHex
            )
        }
    }

    /**
     * ===== Start Game =====
     * เริ่มเกมใหม่: reset state, ลงทะเบียน sensor listener, เริ่ม countdown
     */
    private fun startGame(playerName: String, targetColorHex: String, sens: Float, backgroundColorHex: String) {
        timerJob?.cancel()
        countdownJob?.cancel()
        sensitivity = sens

        // ===== ป้องกันสีเป้าซ้ำกับพื้นหลัง =====
        val safeTargetColor = ensureSafeTargetColor(targetColorHex, backgroundColorHex)

        _state.update {
            GyroscopeGameState(
                playerName = playerName,
                targetColorHex = safeTargetColor,
                countdown = 3,
                isCountingDown = true,
                crosshairX = 0.5f,
                crosshairY = 0.5f,
                targetX = generateRandomPosition(),
                targetY = generateRandomPosition(),
                backgroundColorHex = backgroundColorHex
            )
        }

        // ลงทะเบียน gyroscope sensor
        registerSensor()
        startCountdown()
    }

    /**
     * ===== Register Sensor =====
     * ลงทะเบียน listener สำหรับ gyroscope sensor
     * อัตราการอ่าน: SENSOR_DELAY_GAME (เร็วสุดสำหรับเกม ~20ms)
     */
    private fun registerSensor() {
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    /**
     * ===== Unregister Sensor =====
     * ยกเลิก listener เพื่อประหยัด battery
     */
    private fun unregisterSensor() {
        sensorManager.unregisterListener(this)
    }

    /**
     * ===== Sensor Changed =====
     * ถูกเรียกอัตโนมัติเมื่อ gyroscope ส่งค่าใหม่
     * values[0] = rotation rate รอบแกน X (เอียงหน้า/หลัง)
     * values[1] = rotation rate รอบแกน Y (เอียงซ้าย/ขวา)
     *
     * แปลงค่า rotation rate → ตำแหน่ง crosshair:
     * - ใช้ sensitivity เป็นตัวคูณ
     * - หาร 1000 เพื่อให้ค่าเหมาะสม
     * - clamp ค่าให้อยู่ในช่วง 0.05 - 0.95 (ไม่ให้ออกนอกจอ)
     *
     * ใน landscape mode:
     * - values[1] (แกน Y ของ sensor) → เคลื่อนที่แนวนอน (crosshairX)
     * - values[0] (แกน X ของ sensor) → เคลื่อนที่แนวตั้ง (crosshairY)
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_GYROSCOPE) return
        if (_state.value.isCountingDown || _state.value.isGameOver) return

        val rotX = event.values[0] // rotation rate รอบแกน X
        val rotY = event.values[1] // rotation rate รอบแกน Y

        _state.update { current ->
            // ใน landscape: sensor X → หน้าจอ X (กลับเครื่องหมายเพื่อให้ทิศถูก), sensor Y → หน้าจอ Y
            val newX = (current.crosshairX - rotX * sensitivity / 1000f).coerceIn(0.05f, 0.95f)
            val newY = (current.crosshairY + rotY * sensitivity / 1000f).coerceIn(0.05f, 0.95f)
            current.copy(crosshairX = newX, crosshairY = newY)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ไม่ต้องจัดการ — required by SensorEventListener interface
    }

    /**
     * ===== Countdown =====
     * นับถอยหลัง 3→2→1 เหมือน GameViewModel
     */
    private fun startCountdown() {
        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                _state.update { it.copy(countdown = i) }
                delay(1000)
            }
            _state.update { it.copy(countdown = 0, isCountingDown = false) }
            startTimer()
        }
    }

    /**
     * ===== Timer (60 วินาที) =====
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
     * ===== Handle Fire (กดปุ่มยิง) =====
     * ตรวจสอบว่า crosshair อยู่ใกล้เป้าหรือไม่:
     * - ถ้าระยะห่าง < hitThreshold → HIT: +10 คะแนน, spawn เป้าใหม่ (ไม่ซ้ำตำแหน่ง)
     * - ถ้าระยะห่าง >= hitThreshold → MISS: เพิ่ม missCount
     * คำนวณ accuracy ใหม่ทุกครั้งที่ยิง
     *
     * return: true ถ้ายิงโดน (ใช้ใน UI เพื่อเล่น SFX)
     */
    fun handleFire(): Boolean {
        if (_state.value.isGameOver || _state.value.isCountingDown) return false

        val current = _state.value
        val dx = current.crosshairX - current.targetX
        val dy = current.crosshairY - current.targetY
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        val isHit = distance < hitThreshold
        val newTotalShots = current.totalShots + 1
        val newHitCount = if (isHit) current.hitCount + 1 else current.hitCount
        val newMissCount = if (!isHit) current.missCount + 1 else current.missCount
        val newAccuracy = if (newTotalShots > 0) (newHitCount.toFloat() / newTotalShots * 100f) else 0f

        // ===== ถ้ายิงโดน → spawn เป้าใหม่ห่างจากตำแหน่งเดิมอย่างน้อย minTargetDistance =====
        val newTargetX: Float
        val newTargetY: Float
        if (isHit) {
            val newPos = generateNonRepeatingPosition(current.targetX, current.targetY)
            newTargetX = newPos.first
            newTargetY = newPos.second
        } else {
            newTargetX = current.targetX
            newTargetY = current.targetY
        }

        _state.update {
            it.copy(
                score = if (isHit) it.score + 10 else it.score,
                hitCount = newHitCount,
                missCount = newMissCount,
                totalShots = newTotalShots,
                accuracy = newAccuracy,
                targetX = newTargetX,
                targetY = newTargetY
            )
        }

        return isHit
    }

    /**
     * ===== Generate Random Position =====
     * สร้างตำแหน่ง random (0.1 - 0.9) เพื่อไม่ให้เป้าอยู่ติดขอบจอ
     */
    private fun generateRandomPosition(): Float {
        return 0.1f + Random.nextFloat() * 0.8f
    }

    /**
     * ===== Generate Non-Repeating Position =====
     * สร้างตำแหน่ง random ที่ห่างจากตำแหน่งเดิมอย่างน้อย minTargetDistance
     * ป้องกันเป้าขึ้นที่เดิมซ้ำ
     * ลอง generate ใหม่สูงสุด 20 ครั้ง (fallback ถ้าหาไม่ได้)
     */
    private fun generateNonRepeatingPosition(oldX: Float, oldY: Float): Pair<Float, Float> {
        var attempts = 0
        var newX: Float
        var newY: Float
        do {
            newX = generateRandomPosition()
            newY = generateRandomPosition()
            val dx = newX - oldX
            val dy = newY - oldY
            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            attempts++
        } while (dist < minTargetDistance && attempts < 20)
        return Pair(newX, newY)
    }

    /**
     * ===== End Game =====
     * จบเกม → หยุด timer, ยกเลิก sensor, บันทึกคะแนน
     * Score จะมี gameMode = GYROSCOPE + accuracy
     */
    private fun endGame() {
        timerJob?.cancel()
        unregisterSensor()
        _state.update { it.copy(isGameOver = true) }
        val finalState = _state.value
        viewModelScope.launch {
            val scoreRecord = Score(
                playerName = finalState.playerName.ifEmpty { "UNKNOWN" },
                score = finalState.score,
                gameMode = GameMode.GYROSCOPE.name,  // ← บันทึกว่าเป็นโหมด Gyroscope
                accuracy = finalState.accuracy        // ← บันทึก accuracy
            )
            dataStoreManager.saveScore(scoreRecord)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()
        unregisterSensor()
    }
}
