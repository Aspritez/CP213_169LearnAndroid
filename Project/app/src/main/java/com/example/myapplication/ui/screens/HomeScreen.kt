package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.model.GameMode
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

/**
 * ===== HomeScreen =====
 * หน้าแรกของแอป — ผู้เล่นกรอกชื่อ, เลือกโหมด, แล้วกด START
 *
 * เกี่ยวข้องกับ:
 *   - MainViewModel → อ่าน playerName, selectedMode; เขียนผ่าน callback
 *   - MainActivity (FocusShotApp) → navigate ไปหน้าเกมตาม mode ที่เลือก
 *   - GameMode enum → ตัวเลือกโหมดใน Dropdown
 *
 * Layout: แนวนอน (landscape) — ใช้ Row แบ่งซ้าย (ชื่อ+โลโก้) กับ ขวา (dropdown+ปุ่ม)
 *
 * @param playerName ชื่อผู้เล่นปัจจุบัน ← MainViewModel.playerName
 * @param selectedMode โหมดเกมที่เลือก ← MainViewModel.selectedMode
 * @param onNameChange callback เมื่อชื่อเปลี่ยน → MainViewModel.updatePlayerName()
 * @param onModeChange callback เมื่อโหมดเปลี่ยน → MainViewModel.updateSelectedMode()
 * @param onStartClick callback เมื่อกด START → navigate ไปหน้าเกม
 * @param onSettingsClick callback เมื่อกด Settings → navigate ไปหน้า Settings
 * @param onScoreboardClick callback เมื่อกด Scoreboard → navigate ไปหน้า Scoreboard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerName: String,
    selectedMode: GameMode,
    onNameChange: (String) -> Unit,
    onModeChange: (GameMode) -> Unit,
    onStartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onScoreboardClick: () -> Unit
) {
    // ===== State สำหรับ Dropdown =====
    var dropdownExpanded by remember { mutableStateOf(false) }

    // ===== State สำหรับ Tutorial Dialog =====
    var showTutorialDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // ===== ฝั่งซ้าย: โลโก้ + ชื่อเกม =====
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ===== Game Logo (จากไฟล์ logo.png ใน drawable) =====
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Focus Shot Logo",
                modifier = Modifier.size(240.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ===== ปุ่ม Settings + Scoreboard =====
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ===== ปุ่ม Settings =====
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = PrimaryRed,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = "SETTING",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed
                    )
                }

                // ===== ปุ่ม Scoreboard =====
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = onScoreboardClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Scoreboard",
                            tint = PrimaryRed,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = "SCOREBOARD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed
                    )
                }

                // ===== ปุ่ม Tutorial =====
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    IconButton(
                        onClick = { showTutorialDialog = true },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QuestionMark,
                            contentDescription = "Tutorial",
                            tint = PrimaryRed,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = "TUTORIAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed
                    )

                }


            }
        }

        Spacer(modifier = Modifier.width(32.dp))

        // ===== ฝั่งขวา: กรอกชื่อ + เลือกโหมด + ปุ่ม START =====
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ===== ช่อง NAME =====
            Text(
                text = "NAME :",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = playerName,
                onValueChange = onNameChange,
                modifier = Modifier
                    .width(500.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== Dropdown เลือกโหมด =====
            // ใช้ ExposedDropdownMenuBox จาก Material3
            // ตัวเลือก: Gridshot (โหมดเดิม) หรือ Gyroscope Training (โหมดใหม่)
            Text(
                text = "SELECT MODE :",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                // ===== ช่องแสดงโหมดที่เลือก =====
                OutlinedTextField(
                    value = when (selectedMode) {
                        GameMode.GRIDSHOT -> "Gridshot"
                        GameMode.GYROSCOPE -> "Gyroscope Training"
                    },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .width(500.dp)
                        .height(70.dp)
                        .menuAnchor(),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // ===== รายการตัวเลือกใน Dropdown =====
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Gridshot") },
                        onClick = {
                            onModeChange(GameMode.GRIDSHOT)
                            dropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Gyroscope Training") },
                        onClick = {
                            onModeChange(GameMode.GYROSCOPE)
                            dropdownExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== ปุ่ม START =====
            // ปิดการใช้งานถ้ายังไม่ได้กรอกชื่อ
            Button(
                onClick = {
                    if (playerName.isNotBlank()) onStartClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                shape = RoundedCornerShape(28.dp),
                enabled = playerName.isNotBlank()
            ) {
                Text(text = "START", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }

    // ===== Tutorial Popup Dialog =====
    if (showTutorialDialog) {
        AlertDialog(
            onDismissRequest = { showTutorialDialog = false },
            title = {
                Text(
                    text = "HOW TO PLAY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PrimaryRed
                )
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = " Gridshot Mode", fontWeight = FontWeight.Bold, color = PrimaryRed)
                    Text(text = "• เป้าหมายจะปรากฏขึ้นบนหน้าจอ")
                    Text(text = "• แตะเป้าหมายให้ได้มากที่สุดก่อนหมดเวลา")
                    Text(text = "• แตะถูกเป้า +10 คะแนน")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = " Gyroscope Training Mode", fontWeight = FontWeight.Bold, color = PrimaryRed)
                    Text(text = "• ใช้การเอียงตัวเครื่องเพื่อเล็งเป้าหมาย")
                    Text(text = "• ขยับเครื่องไปทิศต่าง ๆ เพื่อควบคุม crosshair")
                    Text(text = "• แตะหน้าจอเพื่อยิงเมื่อ crosshair อยู่บนเป้า")
                    Text(text = "• แตะถูกเป้า +10 คะแนน")
                    Text(text = "• เมื่อยิงไม่โดนเป้าจะทำให้ความแม่นยำลดลง")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "⭐ กรอกชื่อก่อนกด START เพื่อบันทึกคะแนน")
                }
            },
            confirmButton = {
                TextButton(onClick = { showTutorialDialog = false }) {
                    Text(text = "เข้าใจแล้ว!", fontWeight = FontWeight.Bold, color = PrimaryRed)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

