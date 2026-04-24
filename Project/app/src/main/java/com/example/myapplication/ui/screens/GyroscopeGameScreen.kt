package com.example.myapplication.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.GlassBorder
import com.example.myapplication.ui.theme.GlassWhite
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor
import com.example.myapplication.viewmodel.GyroscopeGameState

/**
 * ===== GyroscopeGameScreen =====
 * หน้าเล่นเกมโหมด Gyroscope Training — ผู้เล่นเอียงเครื่องเพื่อเล็งเป้า
 *
 * เกี่ยวข้องกับ:
 *   - GyroscopeGameState → สถานะเกมจาก GyroscopeViewModel
 *   - GyroscopeViewModel → ส่ง intent ผ่าน callbacks (onFire, onPlayAgainClick)
 *   - AudioController → เรียก playSfx() เมื่อยิงโดน (จัดการใน MainActivity)
 *   - CountdownOverlay → ใช้ร่วมกับ GameScreen (Composable เดียวกัน)
 *
 * Layout: แนวนอน (landscape) บังคับ
 *   - ซ้าย: ข้อมูลผู้เล่น (name, score, hits, misses, accuracy, time)
 *   - กลาง: พื้นที่เกม (Canvas) — แสดงเป้าหมาย + crosshair
 *   - ขวา: ปุ่มยิง (skeleton button) + HOME
 *
 * @param state สถานะเกม ← GyroscopeViewModel.state
 * @param onFire เมื่อกดปุ่มยิง → GyroscopeViewModel.Fire
 * @param onHomeClick กลับหน้า Home
 * @param onScoreboardClick ไปหน้า Scoreboard
 * @param onPlayAgainClick เล่นใหม่
 */
@Composable
fun GyroscopeGameScreen(
    state: GyroscopeGameState,
    onFire: () -> Boolean,  // return true ถ้ายิงโดน (เพื่อเล่น SFX)
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
            // ===== ฝั่งซ้าย: ข้อมูลผู้เล่น + สถิติ =====
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // ชื่อผู้เล่น
                Text("NAME :", color = TextColor, fontSize = 12.sp)
                Text(
                    state.playerName.uppercase(),
                    color = PrimaryRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // คะแนน
                Text("SCORE", color = TextColor, fontSize = 12.sp)
                Text(
                    "${state.score}",
                    color = PrimaryRed,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                // ===== สถิติการยิง =====
                // Hits: จำนวนครั้งที่ยิงโดน
                Text("HITS: ${state.hitCount}", color = Color.Green, fontSize = 14.sp)
                // Misses: จำนวนครั้งที่ยิงพลาด
                Text("MISS: ${state.missCount}", color = Color.Red, fontSize = 14.sp)
                // Accuracy: ความแม่นยำ %
                Text(
                    "ACC: ${"%.1f".format(state.accuracy)}%",
                    color = TextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // ===== Timer =====
                Text("TIME", color = TextColor, fontSize = 12.sp)
                Text(
                    "0:${state.timeLeft.toString().padStart(2, '0')}",
                    color = if (state.timeLeft <= 10) PrimaryRed else TextColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // ===== ตรงกลาง: พื้นที่เกม (Canvas) =====
            // ใช้ Canvas วาดเป้าหมาย (วงกลม) และ Crosshair (กากบาท)
            // ตำแหน่งเป้าหมาย: state.targetX/Y (fraction 0.0-1.0)
            // ตำแหน่ง crosshair: state.crosshairX/Y (ควบคุมโดย gyroscope)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .background(Color(0xFF0D1B2A), RoundedCornerShape(16.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // ===== วาดเป้าหมาย (Target) =====
                    // วงกลมใหญ่ที่ผู้เล่นต้องเล็งไปหา
                    val targetCenter = Offset(
                        x = state.targetX * canvasWidth,
                        y = state.targetY * canvasHeight
                    )
                    val targetRadius = 30.dp.toPx()

                    // วงกลมเป้า (filled)
                    drawCircle(
                        color = targetColor,
                        radius = targetRadius,
                        center = targetCenter
                    )
                    // ขอบเป้า (สีขาว)
                    drawCircle(
                        color = Color.White,
                        radius = targetRadius,
                        center = targetCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // ===== วาด Crosshair (จุดเล็ง) =====
                    // กากบาท + วงกลมเล็ก ตรงตำแหน่ง crosshair
                    val crosshairCenter = Offset(
                        x = state.crosshairX * canvasWidth,
                        y = state.crosshairY * canvasHeight
                    )
                    val crosshairSize = 20.dp.toPx()

                    // เส้นแนวนอน
                    drawLine(
                        color = Color.White,
                        start = Offset(crosshairCenter.x - crosshairSize, crosshairCenter.y),
                        end = Offset(crosshairCenter.x + crosshairSize, crosshairCenter.y),
                        strokeWidth = 2.dp.toPx()
                    )
                    // เส้นแนวตั้ง
                    drawLine(
                        color = Color.White,
                        start = Offset(crosshairCenter.x, crosshairCenter.y - crosshairSize),
                        end = Offset(crosshairCenter.x, crosshairCenter.y + crosshairSize),
                        strokeWidth = 2.dp.toPx()
                    )
                    // วงกลมตรง crosshair
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = crosshairCenter,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // ===== ฝั่งขวา: ปุ่มยิง (Skeleton Button) + HOME =====
            // ปุ่มยิงเป็น skeleton style (ขอบเท่านั้น ไม่มีพื้นหลัง)
            // อยู่ด้านขวาของหน้าจอ เหมือนเกมมือถือ FPS
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ===== ปุ่มยิง (FIRE) =====
                // Skeleton button: ขอบแดง ไม่มีพื้นหลัง
                // กดได้เฉพาะเมื่อไม่อยู่ใน countdown และเกมยังไม่จบ
                OutlinedButton(
                    onClick = { onFire() },
                    modifier = Modifier
                        .size(80.dp),
                    shape = CircleShape,
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 3.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryRed
                    ),
                    enabled = !state.isCountingDown && !state.isGameOver
                ) {
                    Text(
                        "FIRE",
                        color = PrimaryRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = DarkNavy, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // ===== Countdown Overlay =====
        // ใช้ CountdownOverlay composable เดียวกับ GameScreen
        if (state.isCountingDown) {
            CountdownOverlay(count = state.countdown)
        }

        // ===== Game Over Popup =====
        // แสดงเมื่อหมดเวลา — เพิ่ม accuracy, hits, misses เมื่อเทียบกับ Gridshot
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("GYROSCOPE TRAINING", fontSize = 14.sp, color = PrimaryRed)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("name : ${state.playerName}", fontSize = 16.sp, color = TextColor)
                        Text("score : ${state.score}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextColor)

                        Spacer(modifier = Modifier.height(8.dp))

                        // ===== สถิติ Accuracy =====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("HITS", color = Color.Green, fontSize = 12.sp)
                                Text("${state.hitCount}", color = Color.Green, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MISS", color = Color.Red, fontSize = 12.sp)
                                Text("${state.missCount}", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ACCURACY", color = TextColor, fontSize = 12.sp)
                                Text(
                                    "${"%.1f".format(state.accuracy)}%",
                                    color = TextColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onPlayAgainClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("PLAY AGAIN", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                            .size(40.dp)
                                            .clip(CircleShape)
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
