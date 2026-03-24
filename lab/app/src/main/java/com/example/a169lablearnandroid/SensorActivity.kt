package com.example.a169lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class SensorActivity : ComponentActivity() {

    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var hasLocationPermission by remember { 
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context, 
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) 
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                hasLocationPermission = isGranted
                if (isGranted) {
                    viewModel.stopTracking()
                    viewModel.startTracking()
                } else {
                    Toast.makeText(context, "Location Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

            LaunchedEffect(Unit) {
                if (!hasLocationPermission) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SensorScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTracking()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTracking()
    }
}

@Composable
fun SensorScreen(viewModel: SensorViewModel, modifier: Modifier = Modifier) {
    val sensorData by viewModel.sensorData.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Accelerometer Data",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "X: ${"%.2f".format(sensorData.x)}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Y: ${"%.2f".format(sensorData.y)}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Z: ${"%.2f".format(sensorData.z)}",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "GPS Location",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Lat: ${"%.5f".format(sensorData.latitude)}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Lng: ${"%.5f".format(sensorData.longitude)}",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
