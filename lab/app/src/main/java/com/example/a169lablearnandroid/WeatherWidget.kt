package com.example.a169lablearnandroid

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()
}

class WeatherWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WeatherWidgetContent()
            }
        }
    }
}

@Composable
fun WeatherWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
            .clickable(actionRunCallback<RefreshAction>()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "☀️ สภาพอากาศวันนี้",
            style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF2E7D32)), fontSize = 18.sp),
            modifier = GlanceModifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Bangkok, 32°C",
            style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(Color(0xFF1B5E20)), fontSize = 28.sp),
            modifier = GlanceModifier.padding(bottom = 8.dp)
        )
        Text(
            text = "แดดจัด (แตะเพื่ออัปเดต)",
            style = TextStyle(color = ColorProvider(Color(0xFF388E3C)), fontSize = 14.sp)
        )
    }
}

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // ในสถานการณ์จริง จะอัปเดตข้อมูลตรงนี้แล้วสั่งอัปเดต Widget
        WeatherWidget().update(context, glanceId)
    }
}
