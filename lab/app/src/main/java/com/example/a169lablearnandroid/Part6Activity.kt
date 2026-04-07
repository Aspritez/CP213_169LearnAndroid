package com.example.a169lablearnandroid

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Part6Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

class WebViewModel : ViewModel() {
    // 1. สร้าง ViewModel เก็บค่า URL String (ค่าเริ่มต้นคือ Google)
    private val _url = MutableStateFlow("https://www.google.com")
    val url: StateFlow<String> = _url

    fun updateUrl(newUrl: String) {
        var validUrl = newUrl.trim()
        if (!validUrl.startsWith("http://") && !validUrl.startsWith("https://")) {
            validUrl = "https://$validUrl"
        }
        _url.value = validUrl
    }
}

@Composable
fun WebViewScreen(modifier: Modifier = Modifier, viewModel: WebViewModel = viewModel()) {
    // ดึงค่า url มาเป็น state เพื่อรออัปเดต UI เมื่อมีการเปลี่ยนแปลง
    val currentUrl by viewModel.url.collectAsState()
    
    // state สำรองสำหรับช่องกรอกข้อมูลข้อความดิบ
    var inputText by remember { mutableStateOf(currentUrl) }

    Column(modifier = modifier.fillMaxSize()) {
        // 4. สร้าง UI แถบพิมพ์ข้อความ และปุ่มกดเข้าเว็บ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("Enter Website URL") }
            )
            Button(
                onClick = { viewModel.updateUrl(inputText) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Go")
            }
        }

        // 2. ใช้ AndroidView เพื่อเรียก View ดั้งเดิมที่สร้างจาก XML ไฟล์ (layout_webview.xml)
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // Inflate ตัว WebView มาจากไฟล์ XML แทนที่จะ new WebView() แบบเดิม
                val webView = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.layout_webview, null, false) as WebView

                webView.apply {
                    settings.javaScriptEnabled = true
                    // 3. ตั้งค่า WebViewClient ให้รันเว็บบนแอปพลิเคชันเรา (ป้องกันเด้งผ่าย Browser เครื่อง)
                    webViewClient = WebViewClient() 
                }
            },
            update = { webView ->
                // Block นี้จะทำงานทุกครั้งเมื่อ State (currentUrl) มีการเปลี่ยนแปลง ทำให้มันคอย Sync ข้อมูลใหม่เสมอ
                webView.loadUrl(currentUrl)
            }
        )
    }
}