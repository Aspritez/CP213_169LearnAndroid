package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme

class Part9Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                CollapsingToolbarScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingToolbarScreen() {
    // 1. กำหนด ScrollBehavior เพื่อให้ AppBar รู้ว่าเลื่อนจอลงมาแล้ว
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        // 2. ผูกเวลาเลื่อน (Nested Scroll) ของ Scaffold เข้ากับพฤติกรรมตัว AppBar
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Collapsing Toolbar") },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Concept: Collapsing Toolbar เป็น UI ยอดฮิตที่ Header จะถูกบีบ (Collapse) เมื่อผู้ใช้ไถหน้าจอลง และขยายกลับเมื่อไถขึ้นหน้าบนสุด\n\nการทำใน Compose สบายมาก แค่ใช้ LargeTopAppBar ร่วมกับ TopAppBarDefaults.exitUntilCollapsedScrollBehavior() และผูก modifier nestedScroll ก็ใช้งานได้ทันทีเหมือนหน้าจอแอปยุคใหม่!",
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(50) { index ->
                ListItem(
                    headlineContent = { Text("Item #$index") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
