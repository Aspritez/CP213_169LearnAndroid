package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.AppSettings
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    onSettingsChanged: (Boolean, Boolean, String) -> Unit,
    onHomeClick: () -> Unit
) {
    // Parse target color from hex
    val targetColor = try {
        Color(android.graphics.Color.parseColor(appSettings.targetColorHex))
    } catch (e: Exception) {
        PrimaryRed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SETTING",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        // Target Color Preview Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(targetColor)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("preview", color = TextColor)
            }
            
            Button(
                onClick = {
                    // Simple logic to cycle colors for now, since building a full color picker is heavy
                    val nextColor = when (appSettings.targetColorHex.uppercase()) {
                        "#E63946" -> "#F4A261" // Orange
                        "#F4A261" -> "#2A9D8F" // Greenish
                        "#2A9D8F" -> "#00B4D8" // Cyan
                        "#00B4D8" -> "#7209B7" // Purple
                        else -> "#E63946" // Back to original red
                    }
                    onSettingsChanged(appSettings.musicEnabled, appSettings.sfxEnabled, nextColor)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
            ) {
                Text("change\ncolor", color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        // Music Toggle
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("music", color = TextColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Switch(
                checked = appSettings.musicEnabled,
                onCheckedChange = { onSettingsChanged(it, appSettings.sfxEnabled, appSettings.targetColorHex) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryRed
                )
            )
        }

        // SFX Toggle
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("sfx", color = TextColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Switch(
                checked = appSettings.sfxEnabled,
                onCheckedChange = { onSettingsChanged(appSettings.musicEnabled, it, appSettings.targetColorHex) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryRed
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Home Button Pattern
        Box(
            modifier = Modifier
                .align(Alignment.Start) // Alignment based on wireframe (bottom left)
                .clickable(onClick = onHomeClick),
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
}
