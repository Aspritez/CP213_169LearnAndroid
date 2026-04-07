package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme

class Part12Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DialogAndBottomSheetScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAndBottomSheetScreen(modifier: Modifier = Modifier) {
    // ควบคุมสถานะการโชว์ของทั้งสองตัวแสดงผล
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    
    // สถานะสำหรับดูแลควบคุมว่า Bottom Sheet รูดลงจบหรือยัง
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Concept: Modal Bottom Sheet & Middle Dialog\n\n" +
                   "1. Dialog (ไดอะล็อกกลางจอ): เหมาะกับการบังคับให้ผู้ใช้อ่านข้อมูลสำคัญหรือตัดสินใจแบบฉับพลัน (เช่น การกดยืนยันใบเสร็จ ยืนยันการลบแบบกู้คืนไม่ได้)\n\n" +
                   "2. Modal Bottom Sheet (แผ่นงานเด้งจากด้านล่าง): เหมาะสำหรับให้ผู้ใช้ 'เลือกออปชัน' สั้นๆ หรือแสดงข้อมูลเพิ่มเติมโดยไม่รู้สึกโดนบังคับเหมือน Dialog กดยกเลิกง่ายด้วยการปัดนิ้วลงด้านล่างสุดคลาสสิก",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Open Middle Dialog (ลองกดยืนยัน)")
        }

        Button(onClick = { showBottomSheet = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Open Bottom Sheet (ลองปัดขึ้นลง)")
        }
    }

    // Modal Dialog Component (กลางหน้าจอ)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to permanently delete this item? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal Bottom Sheet Component (งอกจากข้างล่าง)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            // โครงสร้างภายใน Bottom Sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Share this file", style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                ListItem(headlineContent = { Text("Share to WhatsApp") })
                ListItem(headlineContent = { Text("Share to Facebook") })
                ListItem(headlineContent = { Text("Copy Link") })
                Spacer(modifier = Modifier.height(32.dp)) // ดันเผื่อขอบจอโค้ง
            }
        }
    }
}
