package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import com.example.a169lablearnandroid.utils.SharedPreferencesUtil

class SharePreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferencesUtil.init(this)

        // การบันทึกค่า (เช่น เมื่อกดปุ่ม Save)
//        SharedPreferencesUtil.saveString("user_name", "Bhakin")
//        SharedPreferencesUtil.saveBoolean("is_dark_mode", true)

        // การดึงค่ามาใช้งาน (เช่น เมื่อเปิดแอพขึ้นมาใหม่)
        val name_user = SharedPreferencesUtil.getString("user_name")
        val darkMode = SharedPreferencesUtil.getBoolean("is_dark_mode")


        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = name_user,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _169LabLearnAndroidTheme {
        Greeting("Android")
    }
}