package com.sensitivitysync.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sensitivitysync.data.PermissionState
import top.yukonga.miuix.kmp.basic.Scafall
import top.yukonga.miuix.kmp.basic.TopAppBar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onRequestShizuku: () -> Unit,
    onRequestMediaProjection: () -> Unit,
    onOpenOverlaySettings: () -> Unit
) {
    val session by viewModel.session.collectAsState()
    val status by viewModel.status.collectAsState()
    val permissions by viewModel.permissions.collectAsState()

    var baseSensA by remember { mutableStateOf("70") }
    var accelA by remember { mutableStateOf("125") }

    Scafall(
        topAppBar = TopAppBar(title = "NekoSense")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    PermissionRow("Shizuku", permissions.shizuku == PermissionState.GRANTED, onRequestShizuku)
                    PermissionRow("Screen Capture", permissions.mediaProjection == PermissionState.GRANTED, onRequestMediaProjection)
                    PermissionRow("Overlay", permissions.overlay == PermissionState.GRANTED, onOpenOverlaySettings)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (permissions.shizuku == PermissionState.GRANTED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = "Game A Settings",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = baseSensA,
                            onValueChange = { baseSensA = it },
                            label = { Text("Base Sensitivity") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = accelA,
                            onValueChange = { accelA = it },
                            label = { Text("Acceleration") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.startCalibration(
                            baseSensA.toFloatOrNull() ?: 70f,
                            accelA.toIntOrNull() ?: 125
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Calibration")
                }

                Spacer(Modifier.height(16.dp))

                if (status.isNotEmpty()) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (granted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(text = if (granted) "Granted" else "Grant")
        }
    }
}
