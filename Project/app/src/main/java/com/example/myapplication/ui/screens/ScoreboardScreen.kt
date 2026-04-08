package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.example.myapplication.data.model.Score
import com.example.myapplication.ui.theme.DarkNavy
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

@Composable
fun ScoreboardScreen(
    scores: List<Score>,
    onHomeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Text(
            text = "SCOREBOARD",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryRed,
            modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("RANK", color = TextColor, fontWeight = FontWeight.Bold)
            Text("NAME", color = TextColor, fontWeight = FontWeight.Bold)
            Text("SCORE", color = TextColor, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(scores) { index, score ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#${index + 1}", color = TextColor, fontSize = 18.sp, modifier = Modifier.weight(1f))
                    Text(score.playerName.uppercase(), color = TextColor, fontSize = 18.sp, modifier = Modifier.weight(2f))
                    Text("${score.score}", color = TextColor, fontSize = 18.sp, modifier = Modifier.weight(1f))
                }
            }
        }
        
        // Home Button Pattern from Wireframe
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
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
