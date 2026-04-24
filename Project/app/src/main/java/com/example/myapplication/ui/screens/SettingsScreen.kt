package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor
import com.example.myapplication.viewmodel.isColorTooSimilar

/**
 * ===== SettingsScreen =====
 * หน้าตั้งค่าแอป — ปรับสีเป้า, สีพื้นหลัง, เสียง SFX, ความไว gyroscope
 *
 * เกี่ยวข้องกับ:
 *   - AppSettings (Models.kt) → ค่าที่แสดงและแก้ไข
 *   - MainViewModel.updateSettings() → บันทึกค่าที่แก้ไขลง DataStore
 *   - AudioController.previewSfx() → ทดลองฟังเสียงก่อนเลือก
 *   - GyroscopeViewModel → ใช้ gyroSensitivity ในการคำนวณ crosshair movement
 *
 * Layout: แนวนอน (landscape) — ใช้ Row แบ่งซ้าย/ขวา
 *
 * @param appSettings การตั้งค่าปัจจุบัน ← MainViewModel.appSettings
 * @param onSettingsChanged callback บันทึกค่าใหม่ → MainViewModel.updateSettings()
 * @param onPreviewSfx callback ทดลองฟังเสียง → AudioController.previewSfx()
 * @param onHomeClick กลับหน้า Home
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onPreviewSfx: (String) -> Unit,
    onHomeClick: () -> Unit
) {
    // ===== Context สำหรับ Toast =====
    val context = LocalContext.current

    // ===== Local state สำหรับ target hex color input =====
    // ใช้ local state เพื่อให้ผู้ใช้พิมพ์ได้อิสระ ก่อนกด apply
    var hexInput by remember(appSettings.targetColorHex) {
        mutableStateOf(appSettings.targetColorHex)
    }

    // แปลง hex เป็น Color สำหรับ preview
    val previewColor = try {
        Color(android.graphics.Color.parseColor(hexInput))
    } catch (e: Exception) {
        PrimaryRed // ถ้า hex ไม่ถูกต้อง ใช้สีแดงเป็น fallback
    }

    // ตรวจสอบว่า hex ถูกต้องหรือไม่
    val isValidHex = try {
        android.graphics.Color.parseColor(hexInput)
        true
    } catch (e: Exception) {
        false
    }

    // ===== Local state สำหรับ background hex color input =====
    var bgHexInput by remember(appSettings.backgroundColorHex) {
        mutableStateOf(appSettings.backgroundColorHex)
    }

    // แปลง background hex เป็น Color สำหรับ preview
    val bgPreviewColor = try {
        Color(android.graphics.Color.parseColor(bgHexInput))
    } catch (e: Exception) {
        DarkNavy
    }

    // ตรวจสอบว่า background hex ถูกต้องหรือไม่
    val isValidBgHex = try {
        android.graphics.Color.parseColor(bgHexInput)
        true
    } catch (e: Exception) {
        false
    }

    // ===== ตรวจสอบว่าสีเป้ากับสีพื้นหลังเหมือนกันหรือคล้ายกัน =====
    val isColorConflict = try {
        if (isValidHex && isValidBgHex) {
            isColorTooSimilar(hexInput, bgHexInput)
        } else false
    } catch (e: Exception) {
        false
    }

    // ===== รายชื่อเสียง SFX ทั้ง 5 =====
    val sfxOptions = listOf("pop", "bell", "drip", "blip", "ting")

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalAlignment = Alignment.Top
    ) {
        // ===== ฝั่งซ้าย: Title + Home =====
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SETTING",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )

            Spacer(modifier = Modifier.weight(1f))

            // ===== HOME Button =====
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
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = DarkNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(24.dp))

        // ===== ฝั่งขวา: ตั้งค่าทั้งหมด (scrollable) =====
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ========================================
            // ส่วนที่ 1: สีเป้าหมาย (Target Color)
            // ========================================
            // ผู้ใช้พิมพ์ hex code เอง (เช่น #FF5733)
            // มี preview วงกลมสีให้ดูแบบ real-time
            Text(
                "TARGET COLOR",
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ===== Preview วงกลมสี =====
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(previewColor)
                        .border(2.dp, Color.White, CircleShape)
                )

                // ===== ช่อง input hex code (จำกัด 7 ตัว: #XXXXXX) =====
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { newValue ->
                        // จำกัดให้ใส่ได้แค่ # + hex chars สูงสุด 7 ตัว (#XXXXXX)
                        val filtered = newValue.filter { it == '#' || it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
                        if (filtered.length <= 7) {
                            hexInput = filtered
                            // ถ้า hex ถูกต้อง → ตรวจสอบว่าซ้ำกับ background ไหม
                            try {
                                android.graphics.Color.parseColor(filtered)
                                // ===== เช็คสีเป้ากับพื้นหลังห้ามเหมือนกัน =====
                                if (isValidBgHex && isColorTooSimilar(filtered, bgHexInput)) {
                                    Toast.makeText(
                                        context,
                                        "⚠ สีเป้าหมายคล้ายกับสีพื้นหลังเกินไป กรุณาใส่สีอื่น",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    onSettingsChanged(appSettings.copy(targetColorHex = filtered))
                                }
                            } catch (e: Exception) {
                                // hex ยังไม่ถูกต้อง → ไม่อัปเดต
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Hex Code (e.g. #E63946)") },
                    singleLine = true,
                    isError = !isValidHex || isColorConflict,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isColorConflict) Color.Red else PrimaryRed,
                        unfocusedBorderColor = if (isColorConflict) Color.Red else Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
            }

            if (!isValidHex) {
                Text(
                    "⚠ Invalid hex code",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            // ===== แสดง warning ถ้าสีซ้ำกัน =====
            if (isColorConflict) {
                Text(
                    "⚠ สีเป้าหมายคล้ายกับสีพื้นหลังเกินไป กรุณาใส่สีอื่น",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // ========================================
            // ส่วนที่ 2: สีพื้นหลังหน้าเกม (Background Color)
            // ========================================
            // ผู้ใช้พิมพ์ hex code สำหรับสีพื้นหลังหน้าเกม
            // มี preview วงกลมสีให้ดูแบบ real-time
            Text(
                "BACKGROUND COLOR",
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ===== Preview วงกลมสีพื้นหลัง =====
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(bgPreviewColor)
                        .border(2.dp, Color.White, CircleShape)
                )

                // ===== ช่อง input background hex code (จำกัด 7 ตัว: #XXXXXX) =====
                OutlinedTextField(
                    value = bgHexInput,
                    onValueChange = { newValue ->
                        // จำกัดให้ใส่ได้แค่ # + hex chars สูงสุด 7 ตัว (#XXXXXX)
                        val filtered = newValue.filter { it == '#' || it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
                        if (filtered.length <= 7) {
                            bgHexInput = filtered
                            // ถ้า hex ถูกต้อง → ตรวจสอบว่าซ้ำกับ target ไหม
                            try {
                                android.graphics.Color.parseColor(filtered)
                                // ===== เช็คสีพื้นหลังกับเป้าห้ามเหมือนกัน =====
                                if (isValidHex && isColorTooSimilar(hexInput, filtered)) {
                                    Toast.makeText(
                                        context,
                                        "⚠ สีพื้นหลังคล้ายกับสีเป้าหมายเกินไป กรุณาใส่สีอื่น",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    onSettingsChanged(appSettings.copy(backgroundColorHex = filtered))
                                }
                            } catch (e: Exception) {
                                // hex ยังไม่ถูกต้อง → ไม่อัปเดต
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    label = { Text("Hex Code (e.g. #0A192F)") },
                    singleLine = true,
                    isError = !isValidBgHex || isColorConflict,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isColorConflict) Color.Red else PrimaryRed,
                        unfocusedBorderColor = if (isColorConflict) Color.Red else Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
            }

            if (!isValidBgHex) {
                Text(
                    "⚠ Invalid hex code",
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // ========================================
            // ส่วนที่ 3: เสียง SFX (Sound Effect)
            // ========================================
            // 5 เสียงให้เลือก: pop, bell, drip, blip, ting
            // กด play icon เพื่อ preview เสียงก่อนเลือก
            // เสียงที่เลือกจะถูกเล่นเมื่อยิงเป้าโดนในเกม
            Text(
                "SOUND EFFECT",
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // ===== รายการเสียง SFX แต่ละตัว =====
            sfxOptions.forEach { sound ->
                val isSelected = appSettings.sfxSound == sound
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) PrimaryRed.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PrimaryRed else Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            // เลือกเสียงนี้ → อัปเดต settings
                            onSettingsChanged(appSettings.copy(sfxSound = sound))
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // ชื่อเสียง
                    Text(
                        sound.uppercase(),
                        color = if (isSelected) PrimaryRed else TextColor,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )

                    // ===== ปุ่ม Preview เสียง =====
                    // กด → เล่นเสียงทดลองผ่าน AudioController.previewSfx()
                    IconButton(
                        onClick = { onPreviewSfx(sound) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Preview $sound",
                            tint = PrimaryRed
                        )
                    }
                }
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // ========================================
            // ส่วนที่ 4: SFX Toggle
            // ========================================
            // เปิด/ปิดเสียง SFX
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SFX", color = TextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Switch(
                    checked = appSettings.sfxEnabled,
                    onCheckedChange = {
                        onSettingsChanged(appSettings.copy(sfxEnabled = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryRed
                    )
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // ========================================
            // ส่วนที่ 5: Gyroscope Sensitivity
            // ========================================
            // แถบเลื่อน (Slider) ปรับความไวของ gyroscope
            // ค่าต่ำ (1.0) = crosshair เคลื่อนที่ช้า (เหมาะสำหรับเล็งละเอียด)
            // ค่าสูง (10.0) = crosshair เคลื่อนที่เร็ว (เหมาะกับ Pro Player)
            // ค่า default = 3.0
            // ค่านี้ถูกส่งไปใช้ใน GyroscopeViewModel.sensitivity
            Text(
                "GYROSCOPE SENSITIVITY",
                color = TextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LOW",
                    color = TextColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Slider(
                    value = appSettings.gyroSensitivity,
                    onValueChange = { newValue ->
                        onSettingsChanged(appSettings.copy(gyroSensitivity = newValue))
                    },
                    valueRange = 5f..15f,
                    steps = 19,  // 0.5 ขั้น (20 steps = 21 values from 5.0 to 15.0)
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryRed,
                        activeTrackColor = PrimaryRed,
                        inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
                Text(
                    "HIGH",
                    color = TextColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            // แสดงค่า sensitivity ปัจจุบัน
            Text(
                text = "Current: ${"%.1f".format(appSettings.gyroSensitivity)}",
                color = PrimaryRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
