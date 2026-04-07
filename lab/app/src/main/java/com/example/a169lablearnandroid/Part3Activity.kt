package com.example.a169lablearnandroid

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import kotlin.math.cos
import kotlin.math.sin

class Part3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CanvasExamplesScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasExamplesScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        // --- 1. Donut Chart ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("1. Donut Chart & RenderEffect", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center) {
                AnimatedGlowCanvas()
                DonutChart(
                    proportions = listOf(30f, 40f, 30f),
                    colors = listOf(Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFFFC107)),
                    modifier = Modifier.size(200.dp)
                )
            }
        }

        // --- 2. Sine Wave Graph ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("2. Sine Wave Graph (Path & Math)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            SineWaveGraph()
        }

        // --- 3. Hexagon Radar Chart ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("3. Hexagonal Radar Chart", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            HexagonRadarChart()
        }
    }
}

@Composable
fun SineWaveGraph() {
    // แอนิเมชันเลื่อนคลื่นไปเรื่อยๆ ตามเฟส
    val infiniteTransition = rememberInfiniteTransition(label = "sine_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val path = Path()

        val points = 200 // ความละเอียดของเส้นคลื่น
        val dx = width / points

        for (i in 0..points) {
            val x = i * dx
            // คำนวณแกน y ด้วย sin(angle)
            val angle = (x / width) * 4f * Math.PI.toFloat() - phase // สร้างคลื่น 2 ลูก
            val y = (height / 2) + sin(angle) * (height / 3f)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF00BCD4),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // วาดแกนกลาง (เส้นประศูนย์กลาง)
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun HexagonRadarChart() {
    // สมมติค่า Status ของตัวละคร มี 6 ด้าน (ค่า max คือ 1.0f)
    val values = listOf(0.8f, 0.6f, 0.9f, 0.5f, 0.7f, 1.0f)
    
    // Animation ค่อยๆ กางสเตตัสออก
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.size(200.dp)) {
        val radius = size.width / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val sides = 6

        // 1. วาดโครงรอบนอกเหมือนตาข่ายใยแมงมุม (Web background)
        for (level in 1..5) {
            val levelRadius = radius * (level / 5f)
            val webPath = Path()
            for (i in 0 until sides) {
                // มุมเริ่มต้นด้านบนที่ 12 นาฬิกา คือ -90 องศา
                val angle = (i * 60 - 90) * (Math.PI / 180f)
                val px = center.x + (levelRadius * cos(angle)).toFloat()
                val py = center.y + (levelRadius * sin(angle)).toFloat()
                if (i == 0) webPath.moveTo(px, py) else webPath.lineTo(px, py)
            }
            webPath.close() // ปิด path ให้เป็นรูปหลายเหลี่ยม
            drawPath(
                path = webPath,
                color = Color.LightGray,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 2. วาดเส้นแกนแต่ละด้านจากจุดศูนย์กลาง
        for (i in 0 until sides) {
            val angle = (i * 60 - 90) * (Math.PI / 180f)
            val px = center.x + (radius * cos(angle)).toFloat()
            val py = center.y + (radius * sin(angle)).toFloat()
            drawLine(
                color = Color.LightGray,
                start = center,
                end = Offset(px, py),
                strokeWidth = 1.dp.toPx()
            )
        }

        // 3. วาดเส้น Graph ส่วนของ Data
        val dataPath = Path()
        for (i in 0 until sides) {
            val angle = (i * 60 - 90) * (Math.PI / 180f)
            // คูณค่าร้อยละของ stat นั้นๆ และคูณกับตัวแอนิเมชัน
            val dataRadius = radius * values[i] * animationProgress.value
            val px = center.x + (dataRadius * cos(angle)).toFloat()
            val py = center.y + (dataRadius * sin(angle)).toFloat()

            if (i == 0) dataPath.moveTo(px, py) else dataPath.lineTo(px, py)
        }
        dataPath.close()

        // เติมสีใสๆ ด้านใน Data Polygon
        drawPath(
            path = dataPath,
            color = Color(0x66FF5722) // สีส้มอมแดงโปร่งใส
        )
        // วาดเส้นขอบนอกของ Data Polygon
        drawPath(
            path = dataPath,
            color = Color(0xFFFF5722),
            style = Stroke(width = 3.dp.toPx(), join = StrokeJoin.Round)
        )
    }
}

// -----------------------------------------------------------------------------------------
// ด้านล่างคือ DonutChart และ Animation เดิมที่เคยเขียนไว้
// -----------------------------------------------------------------------------------------

@Composable
fun AnimatedGlowCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    Canvas(
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = 0.5f 
                renderEffect = BlurEffect(radiusX = 40f, radiusY = 40f, edgeTreatment = TileMode.Clamp)
            }
    ) {
        drawCircle(color = Color(0xFFAA00FF))
    }
}

@Composable
fun DonutChart(
    proportions: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val sweepProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        sweepProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rotate_transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        ),
        label = "continuous_rotation"
    )

    val total = proportions.sum()
    val sweepAngles = proportions.map { 360f * (it / total) }

    Canvas(
        modifier = modifier.graphicsLayer {
            rotationZ = rotation
        }
    ) {
        val animatedTotalSweep = 360f * sweepProgress.value
        var startAngle = -90f
        var currentSweepTotal = 0f

        for ((index, sweepAngle) in sweepAngles.withIndex()) {
            val remainingSweep = animatedTotalSweep - currentSweepTotal
            if (remainingSweep <= 0f) break

            val actualSweep = remainingSweep.coerceAtMost(sweepAngle)

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = actualSweep,
                useCenter = false,
                style = Stroke(width = 50.dp.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweepAngle
            currentSweepTotal += sweepAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CanvasExamplesScreenPreview() {
    _169LabLearnAndroidTheme {
        CanvasExamplesScreen()
    }
}