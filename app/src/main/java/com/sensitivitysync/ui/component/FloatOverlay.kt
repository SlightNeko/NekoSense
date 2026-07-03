package com.sensitivitysync.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.SmallButton
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun CalibrationOverlay(
    status: String,
    progress: Float,
    isActive: Boolean,
    onStartSlow: () -> Unit,
    onStartFast: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .widthIn(max = 320.dp)
            .padding(12.dp),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NekoSense",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE94560)
            )
            Spacer(Modifier.height(8.dp))

            if (progress > 0f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = status,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            if (!isActive) {
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallButton(onClick = onStartSlow) {
                        Text("Slow", fontSize = 12.sp)
                    }
                    SmallButton(onClick = onStartFast) {
                        Text("Fast", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeProgressView(
    speedLabel: String,
    progress: Float,
    statusText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = speedLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
        )
        Text(
            text = statusText,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}
