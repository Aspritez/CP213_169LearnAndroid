package com.example.a169lablearnandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityOptionsCompat

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenuScreen()
        }
    }
}

@Composable
fun MenuScreen() {
    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Menu & Transitions Demo", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), textAlign = TextAlign.Center)

        // 1. Default (No custom code)
        Button(onClick = {
            context.startActivity(Intent(context, SensorActivity::class.java))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("1. Sensor MVVM (Default Transition)")
        }

        // 2. Custom Animation: Fade
        Button(onClick = {
            val intent = Intent(context, GalleryActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context, android.R.anim.fade_in, android.R.anim.fade_out
            )
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("2. Gallery Activity (Fade In/Out)")
        }

        // 3. Custom Animation: Slide In Left
        Button(onClick = {
            val intent = Intent(context, RPGCardActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context, android.R.anim.slide_in_left, android.R.anim.slide_out_right
            )
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("3. RPGCardActivity (Slide In Left)")
        }

        // 4. OverridePendingTransition (Old approach style explicitly called out)
        Button(onClick = {
            val intent = Intent(context, PokedexActivity::class.java)
            context.startActivity(intent)
            @Suppress("DEPRECATION")
            context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("4. PokedexActivity (override slide_in_left)")
        }

        // 5. No Transition / Instant
        Button(onClick = {
            val intent = Intent(context, MainActivity2::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(context, 0, 0)
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("5. MainActivity2 (Instant / No Animation)")
        }

        // 6. Task Launch Behind
        Button(onClick = {
            val intent = Intent(context, SharePreferencesActivity::class.java)
            val options = ActivityOptionsCompat.makeTaskLaunchBehind()
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("6. SharePref (Task Launch Behind)")
        }

        // 7. Make Basic (Standard Intent wrapper)
        Button(onClick = {
            val intent = Intent(context, Part1AnimationActivity::class.java)
            val options = ActivityOptionsCompat.makeBasic()
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("7. Part1AnimationActivity (Basic)")
        }

        // 8. Make Scene Transition (Default Shared Element without elements)
        Button(onClick = {
            val intent = Intent(context, Part2Activity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context)
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("8. Part2Activity (Scene Transition Style)")
        }

        // 9. Mix matching transition with anim
        Button(onClick = {
            val intent = Intent(context, Part3Activity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context, android.R.anim.fade_in, android.R.anim.slide_out_right
            )
            context.startActivity(intent, options.toBundle())
        }, modifier = Modifier.fillMaxWidth()) {
            Text("9. Part3Activity (Fade In / Slide Out Right)")
        }

        // 10. Default System
        Button(onClick = {
            context.startActivity(Intent(context, Part4Activity::class.java))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("10. Part4Activity (System Default)")
        }
        
        // 11. Scale up Alternative (using fade since real scale needs view bounds)
        Button(onClick = {
            val intent = Intent(context, Part5Activity::class.java)
            @Suppress("DEPRECATION")
            context.startActivity(intent)
            context.overridePendingTransition(android.R.anim.fade_in, 0)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("11. Part5Activity (Fade In Only)")
        }

        // 12. Default System
        Button(onClick = {
            context.startActivity(Intent(context, Part6Activity::class.java))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("12. Part6Activity (System Default)")
        }

        // 13. Part8Activity Adaptive Layout
        Button(onClick = {
            context.startActivity(Intent(context, Part8Activity::class.java))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("13. Part8Activity (Responsive/Adaptive)")
        }
    }
}
