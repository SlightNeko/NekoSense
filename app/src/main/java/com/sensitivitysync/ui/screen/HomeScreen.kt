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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
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
    var gameBBase by remember { mutableStateOf("") }
    var gameBAccel by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("NekoSense") }) }
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
                        text = "权限",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(12.dp))

                    PermissionRow("Shizuku", permissions.shizuku == PermissionState.GRANTED, onRequestShizuku)
                    PermissionRow("屏幕录制", permissions.mediaProjection == PermissionState.GRANTED, onRequestMediaProjection)
                    PermissionRow("悬浮窗", permissions.overlay == PermissionState.GRANTED, onOpenOverlaySettings)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (permissions.shizuku == PermissionState.GRANTED &&
                permissions.mediaProjection == PermissionState.GRANTED
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            text = "游戏 A 灵敏度",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = baseSensA,
                            onValueChange = { baseSensA = it },
                            label = { Text("基础灵敏度") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = accelA,
                            onValueChange = { accelA = it },
                            label = { Text("加速度 (%)") },
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
                    Text("开始校准")
                }

                if (status.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (gameBBase.isNotEmpty() || gameBAccel.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "游戏 B 转换结果",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (gameBBase.isNotEmpty()) {
                        Text("基础灵敏度: $gameBBase")
                    }
                    if (gameBAccel.isNotEmpty()) {
                        Text("加速度: $gameBAccel")
                    }
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
            Text(text = if (granted) "已授权" else "授权")
        }
    }
}
