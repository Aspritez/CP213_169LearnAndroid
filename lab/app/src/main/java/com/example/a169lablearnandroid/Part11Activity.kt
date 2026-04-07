package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme

class Part11Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SkeletonLoadingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SkeletonLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Concept: Skeleton Loading (Shimmer Effect)\n\n" +
                   "คือเทคนิคการหลอกผู้ใช้ด้วยกราฟิกเงาสีเทาๆ ที่มีการทำ Animation กระเพื่อม (Shimmer Effect) เลียนแบบการกวาดแสง แทนที่จะใช้ Spinner หมุนรอแบบเดิมๆ \n\n" +
                   "ทำให้ผู้ใช้พอทราบว่าโครงสร้างข้อมูลที่กำลังจะโหลดมาเสร็จนั้นมีหน้าตาอย่างไร ทำให้ผู้ใช้รู้สึกว่าแตะข้อมูลของจริงได้เร็วขึ้น (Perceived Performance)",
            style = MaterialTheme.typography.bodyLarge
        )

        // แสดง Shimmer Item เสมือนข้อมูลรายชื่อกำลังโหลด 3 บรรทัด
        repeat(3) {
            ShimmerListItem()
        }
    }
}

@Composable
fun ShimmerListItem() {
    // กำหนด Animation คลื่นแสงซ้ายไปขวา
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_anim"
    )

    // เฉดสี Shimmer Gradient (เทาเข้ม -> เทาอ่อน -> เทาเข้ม)
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // วงกลมจำลองรูปภาพโปรไฟล์
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(brush) 
        )
        // จำลองข้อความ (กล่องยาว กับ กล่องสั้น)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f) // ความยาวครึ่งนึงแบบสุ่มๆ เพื่อความสมจริง
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}
