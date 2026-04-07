package com.example.a169lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a169lablearnandroid.ui.theme._169LabLearnAndroidTheme
import kotlin.math.roundToInt

class Part4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _169LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GesturesScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GesturesScreen(
    modifier: Modifier = Modifier,
    viewModel: TodoViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        TapGesturesExample()
        DragGestureExample()
        TransformGestureExample()
        SwipeToDismissExample(viewModel)
    }
}

// ------------------------------------------------------------------
// 1. Tap Gestures (Single Click, Double Click, Long Press)
// ------------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TapGesturesExample() {
    var text by remember { mutableStateOf("Tap, Double Tap, or Long Press Me!") }

    Column {
        Text("1. Tap Gestures", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp))
                .combinedClickable(
                    onClick = { text = "Single Tapped!" },
                    onDoubleClick = { text = "Double Tapped!" },
                    onLongClick = { text = "Long Pressed!" }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        }
    }
}

// ------------------------------------------------------------------
// 2. Drag Gesture (ลากวัตถุไปมา)
// ------------------------------------------------------------------
@Composable
fun DragGestureExample() {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column {
        Text("2. Drag Gesture", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .clipToBounds() // ป้องกันไม่ให้วัตถุลากทะลุ Box นี้ออกไปแสดงผลทับ UI อื่น
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(60.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume() // บริโภค event เพื่อบอกว่าเรารับรู้แล้ว
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Star, contentDescription = "Drag", tint = Color.White)
            }
        }
    }
}

// ------------------------------------------------------------------
// 3. Transform Gestures (Pinch to Zoom, Pan, Rotate)
// ------------------------------------------------------------------
@Composable
fun TransformGestureExample() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Column {
        Text("3. Transform Gestures (Zoom & Rotate)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
                .clipToBounds()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .size(100.dp)
                    .background(Color(0xFFFF9800), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, bearing ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f) // จำกัดการซูม
                            rotation += bearing // องศาการหมุน
                            // สำหรับการเลื่อนตามนิ้วเวลาซูมหรือไมรูด
                            offset += pan
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Pinch\nPan\nRotate", color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ------------------------------------------------------------------
// 4. Swipe To Dismiss (ปัดเพื่อลบ) Original Mission 4
// ------------------------------------------------------------------
class TodoViewModel : ViewModel() {
    private val _todoItems = androidx.compose.runtime.mutableStateListOf(
        "Learn Modifier.pointerInput",
        "Implement Swipe-to-Dismiss",
        "Master Jetpack Compose"
    )
    val todoItems: List<String> get() = _todoItems
    fun removeItem(item: String) { _todoItems.remove(item) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissExample(viewModel: TodoViewModel) {
    Column {
        Text("4. Swipe To Dismiss", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (viewModel.todoItems.isEmpty()) {
                Text("All items cleared!", color = Color.Gray, modifier = Modifier.padding(8.dp))
            }
            
            viewModel.todoItems.forEach { item ->
                // ใช้ key(...) แทนการใช้ LazyColumn ที่มี items(key = {it})
                // เพราะถ้าใช้ LazyColumn ซ้อนใน verticalScroll(scrollState) จะเกิด Error
                key(item) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.removeItem(item)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    else -> Color.Transparent
                                }, label = "dismiss_color"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            }
                        },
                        content = {
                            ListItem(
                                headlineContent = { Text(item) },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            )
                        },
                        enableDismissFromStartToEnd = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GesturesScreenPreview() {
    _169LabLearnAndroidTheme {
        GesturesScreen()
    }
}