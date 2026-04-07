package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme

class Part10Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppWidgetConceptScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppWidgetConceptScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("App Widget (Glance ใช้งานจริงแล้ว!)", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Text(
            text = "ตอนนี้แอปของคุณรองรับ Jetpack Glance อย่างเต็มรูปแบบแล้ว!\n\n" +
                   "ในโปรเจกต์ได้มีการเพิ่มไลบรารี `androidx.glance:glance-appwidget` เขียนโค้ด Provider ตัวใหม่ และติดตั้ง Widget ขึ้นมาจริงๆ ในชื่อไฟล์ `WeatherWidget.kt` \n\n" +
                   "สิ่งที่คุณต้องทำเพื่อดูผลลัพธ์:\n" +
                   "1. กดรันแอปพลิเคชัน (หรือ Sync Gradle ก่อนเพื่อดึง Dependency เข้าโปรเจกต์)\n" +
                   "2. กดปุ่มโฮม (Home) ออกจากแอป ไปที่หน้าจอหลักของโทรศัพท์\n" +
                   "3. กดค้างที่หน้าจอว่างๆ แล้วเลือกปุ่ม 'Widgets' (วิดเจ็ต)\n" +
                   "4. เลื่อนหาแอปนี้ (169LabLearnAndroid) คุณจะเจอวิดเจ็ตสภาพอากาศ\n" +
                   "5. ลากออกมาวางบนหน้าจอ Home Screen ได้ทันที!\n\n" +
                   "(ด้านล่างคือภาพจำลองวิดเจ็ตที่ผมเขียนเตรียมรอคุณไว้ใน Component Glance เรียบร้อยแล้ว ☀️)",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // จำลองการวาด Widget ของ Glance (Mocking view behavior)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE8F5E9))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Text("☀️ สภาพอากาศวันนี้", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("Bangkok, 32°C", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                Text("แดดจัด พกร่มกันยูวีด้วย", color = Color(0xFF388E3C))
            }
        }
    }
}
