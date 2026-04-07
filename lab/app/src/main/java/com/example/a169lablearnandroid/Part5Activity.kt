package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class Part5Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                SideEffectScreen()
            }
        }
    }
}

class SideEffectViewModel : ViewModel() {
    // ใช้ Channel สำหรับส่งข้อมูลที่ต้องการให้เป็น "One-time event" (อ่านครั้งเดียวหายไป เช่น Snackbar, Toast)
    private val _errorChannel = Channel<String>()
    val errorFlow = _errorChannel.receiveAsFlow()

    fun triggerError() {
        viewModelScope.launch {
            // จำลองการโหลดแบบหน่วงเวลา 500ms
            delay(500)
            // ส่งข้อความไปที่ Channel ว่าเกิด Error ขึ้น
            _errorChannel.send("เกิดข้อผิดพลาดในการโหลดข้อมูล! (Error 500)")
        }
    }
}

@Composable
fun SideEffectScreen(viewModel: SideEffectViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // สร้าง State สำหรับจองคิวการแสดง Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. LaunchedEffect: 
    // ตัวช่วยดักจับข้อมูล Channel Flow จาก ViewModel
    // ถ้าเราปล่อยให้ Flow ถูกเรียกข้างนอกตรงๆ UI มันจะทำงานผิดพลาด (ควรอยู่ใน Coroutine)
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { errorMessage ->
            // เมื่อได้รับข้อมูล Error ให้สั่งโชว์ Snackbar
            snackbarHostState.showSnackbar(message = errorMessage)
        }
    }

    // 2. DisposableEffect:
    // กลไกสำหรับจอง/คืนทรัพยากร มักใช้กับการพ่วง Listener ของ Android ปกติ
    DisposableEffect(Unit) {
        println("SideEffectScreen: ถูกสร้างขึ้นมา (Composition)")
        onDispose {
            // โค้ดตรงนี้จะถูกรันเมื่อผู้ใช้กดปิดหน้าหรือออกจากหน้านี้ไป (ทำลาย Composable)
            println("SideEffectScreen: ถูกทำลาย (Clear resources / Remove Listeners)")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // ผูก SnackbarHost ของ Scaffold เข้ากับ state ที่เราเปิดรอรับคิวไว้
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Compose Side Effects", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("LaunchedEffect & DisposableEffect")
                
                Button(onClick = { viewModel.triggerError() }) {
                    Text("Trigger Error (Demo Request)")
                }
                
                Text(
                    text = "กดปุ่มเพื่อจำลองเหตุการณ์ยิงผ่าน ViewModel \nแล้วเปิด Snackbar โดยไม่ใช้ UI State ธรรมดา",
                    fontSize = 12.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SideEffectScreenPreview() {
    _169LabLearnAndroidTheme {
        SideEffectScreen()
    }
}