package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodel.GameState
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.GlassBorder
import com.example.myapplication.ui.theme.GlassWhite
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

@Composable
fun GameScreen(
    state: GameState,
    onTargetClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onScoreboardClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {
    val targetColor = try {
        Color(android.graphics.Color.parseColor(state.targetColorHex))
    } catch (e: Exception) {
        PrimaryRed
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NAME : ${state.playerName}",
                    color = TextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Time : 0:${state.timeLeft.toString().padStart(2, '0')}",
                    color = TextColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Grid 3x4
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until 4) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val isVisible = state.grid.getOrNull(index) ?: false
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(if (isVisible) targetColor else Color.Transparent)
                                        .clickable(enabled = isVisible && !state.isGameOver) {
                                            onTargetClick(index)
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // Home Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier.clickable(onClick = onHomeClick),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HOME", color = TextColor, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = DarkNavy)
                        }
                    }
                }
            }
        }

        // Glassmorphism Popup on Game Edit
        if (state.isGameOver) {
            // Background overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                // Glassmorphism Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(GlassWhite, RoundedCornerShape(24.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TIME UP",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "name : ${state.playerName}",
                            fontSize = 20.sp,
                            color = TextColor
                        )
                        Text(
                            text = "score : ${state.score}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = onPlayAgainClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PLAY AGAIN", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier.clickable(onClick = onHomeClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("HOME", color = TextColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.Transparent)
                                            .border(2.dp, PrimaryRed, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Home, contentDescription = "Home", tint = PrimaryRed)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier.clickable(onClick = onScoreboardClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SCORE", color = TextColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(Color.Transparent)
                                            .border(2.dp, PrimaryRed, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Scoreboard", tint = PrimaryRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
