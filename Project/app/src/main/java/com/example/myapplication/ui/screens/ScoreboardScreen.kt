package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.GameMode
import com.example.myapplication.data.model.Score
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

/**
 * ===== ScoreboardScreen =====
 * หน้า Leaderboard — แสดงอันดับคะแนนแยกตามโหมด
 *
 * เกี่ยวข้องกับ:
 *   - Score (Models.kt) → ข้อมูลคะแนนแต่ละรอบ (มี gameMode เพื่อ filter)
 *   - MainViewModel.scores → Flow ของคะแนนทั้งหมดจาก DataStore
 *   - GameMode enum → ใช้สลับ tab (GRIDSHOT / GYROSCOPE)
 *
 * Layout: แนวนอน (landscape)
 *   - ซ้าย: title + HOME button
 *   - ขวา: tab selector + ตาราง leaderboard
 *
 * @param scores คะแนนทั้งหมด (ทุกโหมด) ← MainViewModel.scores
 * @param onHomeClick กลับหน้า Home
 */
@Composable
fun ScoreboardScreen(
    scores: List<Score>,
    onHomeClick: () -> Unit
) {
    // ===== State: Tab ที่เลือก =====
    // 0 = Gridshot, 1 = Gyroscope Training
    var selectedTab by remember { mutableIntStateOf(0) }

    // ===== Filter คะแนนตามโหมดที่เลือก =====
    // Score.gameMode เป็น String → เทียบกับ GameMode.name
    val filteredScores = scores.filter { score ->
        when (selectedTab) {
            0 -> score.gameMode == GameMode.GRIDSHOT.name || score.gameMode.isEmpty()
            // gameMode ว่าง = คะแนนเก่าก่อนมีระบบ mode → ถือเป็น Gridshot
            1 -> score.gameMode == GameMode.GYROSCOPE.name
            else -> true
        }
    }.sortedByDescending { it.score } // เรียงจากมากไปน้อย

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // ===== ฝั่งซ้าย: Title + HOME =====
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SCORE\nBOARD",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // HOME Button
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
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

        Spacer(modifier = Modifier.width(16.dp))

        // ===== ฝั่งขวา: Tab + Leaderboard =====
        Column(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight()
        ) {
            // ===== Tab Selector =====
            // สลับระหว่าง Gridshot กับ Gyroscope Training
            // Tab ที่เลือกจะมีสีแดง + ขีดเส้นใต้
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = PrimaryRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryRed
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "GRIDSHOT",
                            color = if (selectedTab == 0) PrimaryRed else TextColor.copy(alpha = 0.5f),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "GYROSCOPE",
                            color = if (selectedTab == 1) PrimaryRed else TextColor.copy(alpha = 0.5f),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ===== Header Row =====
            // คอลัมน์ที่แสดงแตกต่างกันตามโหมด:
            // Gridshot: RANK, NAME, SCORE
            // Gyroscope: RANK, NAME, SCORE, ACC (accuracy)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("RANK", color = TextColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("NAME", color = TextColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                Text("SCORE", color = TextColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                // ===== เฉพาะ Gyroscope: แสดงคอลัมน์ Accuracy =====
                if (selectedTab == 1) {
                    Text("ACC", color = TextColor, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ===== รายการคะแนน =====
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(filteredScores) { index, score ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // อันดับ
                        Text(
                            "#${index + 1}",
                            color = when (index) {
                                0 -> Color(0xFFFFD700) // Gold
                                1 -> Color(0xFFC0C0C0) // Silver
                                2 -> Color(0xFFCD7F32) // Bronze
                                else -> TextColor
                            },
                            fontSize = 16.sp,
                            fontWeight = if (index < 3) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(0.6f)
                        )
                        // ชื่อ
                        Text(
                            score.playerName.uppercase(),
                            color = TextColor,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1.2f)
                        )
                        // คะแนน
                        Text(
                            "${score.score}",
                            color = TextColor,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        // ===== เฉพาะ Gyroscope: แสดง Accuracy =====
                        if (selectedTab == 1) {
                            Text(
                                "${"%.1f".format(score.accuracy)}%",
                                color = TextColor,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(0.8f)
                            )
                        }
                    }
                }

                // ===== ข้อความเมื่อไม่มีคะแนน =====
                if (filteredScores.isEmpty()) {
                    item {
                        Text(
                            text = "No scores yet!",
                            color = TextColor.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
