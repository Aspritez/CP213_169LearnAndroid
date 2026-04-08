package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.myapplication.ui.screens.GameScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.ScoreboardScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.GameIntent
import com.example.myapplication.viewmodel.GameViewModel
import com.example.myapplication.viewmodel.MainViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    private lateinit var audioController: AudioController
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        audioController = AudioController(this)
        dataStoreManager = DataStoreManager(this)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FocusShotApp(dataStoreManager, audioController)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioController.release()
    }
}

@Composable
fun FocusShotApp(dataStoreManager: DataStoreManager, audioController: AudioController) {
    val navController = rememberNavController()

    // Pass DataStoreManager via factory
    val mainViewModel: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(dataStoreManager) as T
        }
    })

    val appSettings by mainViewModel.appSettings.collectAsState()
    val scores by mainViewModel.scores.collectAsState()
    val playerName by mainViewModel.playerName.collectAsState()

    // Update AudioController when settings change
    DisposableEffect(appSettings) {
        audioController.updateSettings(appSettings.sfxEnabled, appSettings.musicEnabled)
        onDispose { }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                playerName = playerName,
                onNameChange = { mainViewModel.updatePlayerName(it) },
                onStartClick = { navController.navigate("game") },
                onSettingsClick = { navController.navigate("settings") }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                appSettings = appSettings,
                onSettingsChanged = { music, sfx, color ->
                    mainViewModel.updateSettings(music, sfx, color)
                },
                onHomeClick = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                } }
            )
        }
        
        composable("scoreboard") {
            ScoreboardScreen(
                scores = scores,
                onHomeClick = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                } }
            )
        }
        
        composable("game") {
            val gameViewModel: GameViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(dataStoreManager) as T
                }
            })
            val gameState by gameViewModel.state.collectAsState()

            // Initialize game state on launch
            DisposableEffect(Unit) {
                gameViewModel.processIntent(GameIntent.StartGame(playerName, appSettings.targetColorHex))
                onDispose { }
            }

            GameScreen(
                state = gameState,
                onTargetClick = { index ->
                    audioController.playSfx()
                    gameViewModel.processIntent(GameIntent.ClickTarget(index))
                },
                onHomeClick = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                } },
                onScoreboardClick = { navController.navigate("scoreboard") {
                    popUpTo("home")
                } },
                onPlayAgainClick = {
                    gameViewModel.processIntent(GameIntent.PlayAgain)
                }
            )
        }
    }
}