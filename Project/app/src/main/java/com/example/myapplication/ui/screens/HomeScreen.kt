package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerName: String,
    selectedMode: GameMode,
    onNameChange: (String) -> Unit,
    onModeChange: (GameMode) -> Unit,
    onStartClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // ===== State สำหรับ Dropdown =====
    var dropdownExpanded by remember { mutableStateOf(false) }

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
            Text(
                text = "FOCUS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            Text(
                text = "SHOT",
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = PrimaryRed
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== ปุ่ม Settings =====
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
                modifier = Modifier.fillMaxWidth(),
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
                        .fillMaxWidth()
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
}
