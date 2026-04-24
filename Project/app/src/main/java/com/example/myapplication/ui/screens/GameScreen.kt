package com.example.myapplication.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

/**
 * ===== GameScreen =====
 * หน้าเล่นเกมโหมด Gridshot — แสดง grid 3x4, timer, score, countdown
 *
 * เกี่ยวข้องกับ:
 *   - GameState → สถานะเกมจาก GameViewModel
 *   - GameViewModel → ส่ง intent ผ่าน callbacks (onTargetClick, onPlayAgainClick)
 *   - AudioController → เรียก playSfx() ผ่าน onTargetClick (จัดการใน MainActivity)
 *
 * Layout: แนวนอน (landscape)
 *   - ซ้าย: ข้อมูลผู้เล่น + HOME button
 *   - กลาง: Grid 3x4
 *   - ขวา: Timer
 *
 * @param state สถานะเกมปัจจุบัน ← GameViewModel.state
 * @param onTargetClick เมื่อกดเป้า → GameViewModel.ClickTarget + AudioController.playSfx()
 * @param onHomeClick กลับหน้า Home
 * @param onScoreboardClick ไปหน้า Scoreboard
 * @param onPlayAgainClick เล่นใหม่ → GameViewModel.PlayAgain
 */
@Composable
fun GameScreen(
    state: GameState,
    onTargetClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onScoreboardClick: () -> Unit,
    onPlayAgainClick: () -> Unit
) {
    // ===== แปลง hex color เป็น Compose Color =====
    val targetColor = try {
        Color(android.graphics.Color.parseColor(state.targetColorHex))
    } catch (e: Exception) {
        PrimaryRed
    }

    // ===== แปลง background hex color =====
    val backgroundColor = try {
        Color(android.graphics.Color.parseColor(state.backgroundColorHex))
    } catch (e: Exception) {
        DarkNavy
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {

        // ===== Main Game Layout (Landscape) =====
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ===== ฝั่งซ้าย: ข้อมูลผู้เล่น + HOME =====
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ชื่อผู้เล่น
                Text(
                    text = "NAME :",
                    color = TextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.playerName.uppercase(),
                    color = PrimaryRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                // ===== Score =====
                Text(
                    text = "SCORE",
                    color = TextColor,
                    fontSize = 14.sp
                )
                Text(
                    text = "${state.score}",
                    color = PrimaryRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                // ===== HOME Button =====
                Box(
                    modifier = Modifier.clickable(onClick = onHomeClick),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("HOME", color = TextColor, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = DarkNavy, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // ===== ตรงกลาง: Grid 3x4 =====
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until 4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val isVisible = state.grid.getOrNull(index) ?: false
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(if (isVisible) targetColor else Color.Transparent)
                                        .clickable(enabled = isVisible && !state.isGameOver && !state.isCountingDown) {
                                            onTargetClick(index)
                                        }
                                )
                            }
                        }
                    }
                }
            }

            // ===== ฝั่งขวา: Timer =====
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "TIME",
                    color = TextColor,
                    fontSize = 14.sp
                )
                Text(
                    text = "0:${state.timeLeft.toString().padStart(2, '0')}",
                    color = if (state.timeLeft <= 10) PrimaryRed else TextColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ===== Countdown Overlay (3, 2, 1) =====
        // แสดงตัวเลขนับถอยหลังก่อนเริ่มเกม
        // isCountingDown = true จาก GameViewModel ระหว่าง countdown
        if (state.isCountingDown) {
            CountdownOverlay(count = state.countdown)
        }

        // ===== Game Over Popup =====
        // แสดงเมื่อหมดเวลา — glassmorphism card พร้อมคะแนน
        if (state.isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .background(GlassWhite, RoundedCornerShape(24.dp))
                        .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TIME UP", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("name : ${state.playerName}", fontSize = 18.sp, color = TextColor)
                        Text("score : ${state.score}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextColor)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onPlayAgainClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PLAY AGAIN", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // ===== HOME icon button =====
                            Box(
                                modifier = Modifier.clickable(onClick = onHomeClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("HOME", color = TextColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, PrimaryRed, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Home, contentDescription = "Home", tint = PrimaryRed)
                                    }
                                }
                            }

                            // ===== SCORE icon button =====
                            Box(
                                modifier = Modifier.clickable(onClick = onScoreboardClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("SCORE", color = TextColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
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

/**
 * ===== CountdownOverlay =====
 * แสดงตัวเลขนับถอยหลัง (3, 2, 1) ตรงกลางจอ
 * มี animation scale (ย่อ→ขยาย) เพื่อให้ดูน่าสนใจ
 *
 * ใช้ใน: GameScreen (Gridshot) และ GyroscopeGameScreen (Gyroscope Training)
 *
 * @param count ตัวเลขที่จะแสดง (3, 2, หรือ 1)
 */
@Composable
fun CountdownOverlay(count: Int) {
    // ===== Scale Animation =====
    // ทุกครั้งที่ count เปลี่ยน → animate จาก scale 0.5 → 1.5
    val scale by animateFloatAsState(
        targetValue = if (count > 0) 1.5f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "countdown_scale"
    )

    if (count > 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$count",
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed,
                modifier = Modifier.scale(scale)
            )
        }
    }
}
