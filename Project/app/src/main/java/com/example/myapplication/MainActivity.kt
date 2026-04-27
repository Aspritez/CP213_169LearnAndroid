package com.example.myapplication

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.audio.AudioController
import com.example.myapplication.data.local.DataStoreManager
import com.example.myapplication.data.model.GameMode
import com.example.myapplication.ui.screens.GameScreen
import com.example.myapplication.ui.screens.GyroscopeGameScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.ScoreboardScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.GameIntent
import com.example.myapplication.viewmodel.GameViewModel
import com.example.myapplication.viewmodel.GyroscopeIntent
import com.example.myapplication.viewmodel.GyroscopeViewModel
import com.example.myapplication.viewmodel.MainViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ===== MainActivity =====
 * Activity หลักของแอป Focus Shot
 *
 * หน้าที่:
 *   - สร้าง AudioController (จัดการเสียง) — ใช้ทั้งแอป
 *   - สร้าง DataStoreManager (จัดการ local storage) — ใช้ทั้งแอป
 *   - บังคับ landscape ผ่าน AndroidManifest.xml
 *   - เข้า FocusShotApp composable ที่จัดการ navigation ทั้งหมด
 */
class MainActivity : ComponentActivity() {

    private lateinit var audioController: AudioController
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioController = AudioController(this)
        dataStoreManager = DataStoreManager(this)

        enableEdgeToEdge()
        enableImmersiveMode()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FocusShotApp(dataStoreManager, audioController)
                }
            }
        }
    }

    /**
     * บังคับ Immersive Mode ทุกครั้งที่ window ได้รับ focus กลับมา
     * (เช่น หลังกด Home แล้วกลับมา, หรือหลังดึง notification shade)
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enableImmersiveMode()
        }
    }

    /**
     * ===== enableImmersiveMode =====
     * ซ่อน Status Bar และ Navigation Bar แบบ Sticky Immersive
     * โดยใช้ WindowInsetsController (API 30+)
     *
     * พฤติกรรม:
     *   - ซ่อนแถบสถานะ (Status Bar) และแถบนำทาง (Navigation Bar)
     *   - ผู้ใช้ swipe จากขอบเพื่อแสดงแถบชั่วคราว แล้วซ่อนอีกครั้งอัตโนมัติ
     */
    private fun enableImmersiveMode() {
        window.insetsController?.let { controller ->
            controller.hide(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioController.release()
    }
}

/**
 * ===== FocusShotApp =====
 * Composable หลักที่จัดการ Navigation ของทั้งแอป
 *
 * Routes:
 *   - "home" → HomeScreen (เลือกโหมด, กรอกชื่อ)
 *   - "settings" → SettingsScreen (ตั้งค่า)
 *   - "scoreboard" → ScoreboardScreen (leaderboard แยกโหมด)
 *   - "game" → GameScreen (Gridshot mode)
 *   - "gyroscope_game" → GyroscopeGameScreen (Gyroscope Training mode)
 *
 * เกี่ยวข้องกับ:
 *   - MainViewModel → state ระดับ app-wide (settings, scores, playerName, selectedMode)
 *   - GameViewModel → state เกม Gridshot
 *   - GyroscopeViewModel → state เกม Gyroscope Training
 *   - AudioController → เล่นเสียง SFX (ส่งผ่าน callback ไปยัง screen)
 *   - DataStoreManager → ส่งให้ ViewModel ผ่าน Factory
 */
@Composable
fun FocusShotApp(dataStoreManager: DataStoreManager, audioController: AudioController) {
    val navController = rememberNavController()

    // ===== MainViewModel =====
    // สร้างด้วย Factory เพราะต้องส่ง DataStoreManager เข้าไป
    val mainViewModel: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(dataStoreManager) as T
        }
    })

    // ===== Observe app-wide state =====
    val appSettings by mainViewModel.appSettings.collectAsState()
    val scores by mainViewModel.scores.collectAsState()
    val playerName by mainViewModel.playerName.collectAsState()
    val selectedMode by mainViewModel.selectedMode.collectAsState()

    // ===== อัปเดต AudioController เมื่อ settings เปลี่ยน =====
    // DisposableEffect จะ re-run เมื่อ appSettings เปลี่ยนค่า
    DisposableEffect(appSettings) {
        audioController.updateSettings(appSettings.sfxEnabled, appSettings.musicEnabled)
        audioController.updateSfxSound(appSettings.sfxSound)
        onDispose { }
    }

    // ===== Navigation Graph =====
    NavHost(navController = navController, startDestination = "home") {

        // ===== หน้า Home =====
        // เลือกโหมด + กรอกชื่อ + กด START
        // เมื่อกด START → navigate ไปหน้าเกมตามโหมดที่เลือก
        composable("home") {
            HomeScreen(
                playerName = playerName,
                selectedMode = selectedMode,
                onNameChange = { mainViewModel.updatePlayerName(it) },
                onModeChange = { mainViewModel.updateSelectedMode(it) },
                onStartClick = {
                    // Navigate ไปหน้าเกมตามโหมดที่เลือก
                    when (selectedMode) {
                        GameMode.GRIDSHOT -> navController.navigate("game")
                        GameMode.GYROSCOPE -> navController.navigate("gyroscope_game")
                    }
                },
                onSettingsClick = { navController.navigate("settings") },
                onScoreboardClick = {
                    navController.navigate("scoreboard") {
                        popUpTo("home")
                    }
                }
            )
        }

        // ===== หน้า Settings =====
        // ตั้งค่าสีเป้า, เสียง SFX, เพลง, ความไว gyroscope
        composable("settings") {
            SettingsScreen(
                appSettings = appSettings,
                onSettingsChanged = { newSettings ->
                    mainViewModel.updateSettings(newSettings)
                },
                onPreviewSfx = { sound ->
                    audioController.previewSfx(sound)
                },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // ===== หน้า Scoreboard =====
        // แสดง leaderboard แยกตามโหมด (tab)
        composable("scoreboard") {
            ScoreboardScreen(
                scores = scores,
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // ===== หน้าเกม Gridshot =====
        // เกม grid 3x4 กดเป้า 60 วินาที
        composable("game") {
            // สร้าง GameViewModel ด้วย Factory
            val gameViewModel: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(dataStoreManager) as T
                }
            })
            val gameState by gameViewModel.state.collectAsState()

            // ===== เริ่มเกมเมื่อเข้าหน้านี้ =====
            // DisposableEffect(Unit) จะ run ครั้งเดียวเมื่อ composable เข้าสู่ composition
            DisposableEffect(Unit) {
                gameViewModel.processIntent(GameIntent.StartGame(
                    playerName = playerName,
                    targetColorHex = appSettings.targetColorHex,
                    backgroundColorHex = appSettings.backgroundColorHex
                ))
                onDispose { }
            }

            GameScreen(
                state = gameState,
                onTargetClick = { index ->
                    // เล่นเสียง SFX + ส่ง intent ClickTarget
                    audioController.playSfx()
                    gameViewModel.processIntent(GameIntent.ClickTarget(index))
                },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onScoreboardClick = {
                    navController.navigate("scoreboard") {
                        popUpTo("home")
                    }
                },
                onPlayAgainClick = {
                    gameViewModel.processIntent(GameIntent.PlayAgain)
                }
            )
        }

        // ===== หน้าเกม Gyroscope Training =====
        // เกมยิงเป้าด้วย gyroscope 60 วินาที
        composable("gyroscope_game") {
            // สร้าง GyroscopeViewModel ด้วย Factory
            // ต้องส่ง Context เพิ่มเพราะใช้ SensorManager
            val gyroViewModel: GyroscopeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GyroscopeViewModel(dataStoreManager, navController.context) as T
                }
            })
            val gyroState by gyroViewModel.state.collectAsState()

            // ===== เริ่มเกมเมื่อเข้าหน้านี้ =====
            // ส่ง sensitivity จาก appSettings
            DisposableEffect(Unit) {
                gyroViewModel.processIntent(
                    GyroscopeIntent.StartGame(
                        playerName = playerName,
                        targetColorHex = appSettings.targetColorHex,
                        sensitivity = appSettings.gyroSensitivity,
                        backgroundColorHex = appSettings.backgroundColorHex
                    )
                )
                onDispose { }
            }

            GyroscopeGameScreen(
                state = gyroState,
                onFire = {
                    // กดยิง → ตรวจสอบ hit/miss
                    val isHit = gyroViewModel.handleFire()
                    if (isHit) {
                        // ยิงโดน → เล่นเสียง SFX
                        audioController.playSfx()
                    }
                    isHit
                },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onScoreboardClick = {
                    navController.navigate("scoreboard") {
                        popUpTo("home")
                    }
                },
                onPlayAgainClick = {
                    gyroViewModel.processIntent(GyroscopeIntent.PlayAgain)
                }
            )
        }
    }
}