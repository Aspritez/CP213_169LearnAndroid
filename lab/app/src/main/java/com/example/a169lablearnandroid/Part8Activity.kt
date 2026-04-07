package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme

class Part8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdaptiveProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AdaptiveProfileScreen(modifier: Modifier = Modifier) {
    // ใช้ BoxWithConstraints เพื่อเข้าถึง property maxWidth และ maxHeight 
    // สำหรับทำ Responsive Design แบบไม่ต้องสนใจขนาดหน้าจอที่แท้จริง
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (maxWidth < 600.dp) {
            // จอแคบ (มือถือแนวตั้ง): เรียงแบบบนลงล่างด้วย Column
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfilePicture(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp))
                Spacer(modifier = Modifier.height(16.dp))
                ProfileInfo(modifier = Modifier.fillMaxWidth())
            }
        } else {
            // จอกว้าง (แท็บเล็ต หรือมือถือแนวนอน): เรียงซ้ายขวาด้วย Row
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfilePicture(modifier = Modifier
                    .weight(1f) // แบ่งสัดส่วน 1 ส่วน
                    .height(300.dp))
                Spacer(modifier = Modifier.width(24.dp))
                ProfileInfo(modifier = Modifier.weight(1.5f)) // แบ่งสัดส่วน 1.5 ส่วน (กว้างกว่ารูป)
            }
        }
    }
}

@Composable
fun ProfilePicture(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.LightGray, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Picture", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Composable
fun ProfileInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("ข้อมูลส่วนตัว (Profile)", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("ชื่อ: สมชาย ใจดี\nอายุ: 28 ปี\nอาชีพ: Android Developer", fontSize = 16.sp, lineHeight = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "รายละเอียด: ชอบเขียนโค้ด Jetpack Compose และทดลองทำ Responsive UI ให้รองรับหลายหน้าจอ ไม่ว่าจะเป็นหน้าจอมือถือเล็กๆ หรือหน้าจอแท็บเล็ตที่ใหญ่ขึ้น",
            fontSize = 14.sp, 
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun AdaptiveProfileMobilePreview() {
    _169LabLearnAndroidTheme {
        AdaptiveProfileScreen()
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun AdaptiveProfileTabletPreview() {
    _169LabLearnAndroidTheme {
        AdaptiveProfileScreen()
    }
}