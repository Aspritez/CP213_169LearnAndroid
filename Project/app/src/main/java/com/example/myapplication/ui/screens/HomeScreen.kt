package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.PrimaryRed
import com.example.myapplication.ui.theme.TextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerName: String,
    onNameChange: (String) -> Unit,
    onStartClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
        
        Spacer(modifier = Modifier.height(64.dp))
        
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                if(playerName.isNotBlank()) onStartClick()
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
        
        Spacer(modifier = Modifier.height(32.dp))
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(56.dp) // Adjusted size for clear clickable area
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = PrimaryRed,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
