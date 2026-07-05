package com.sensitivitysync.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalibrationOverlay(
    status: String,
    progress: Float,
    isActive: Boolean,
    onStartSlow: () -> Unit,
    onStartFast: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .widthIn(max = 320.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE61A1A2E)
        )
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
                    Button(
                        onClick = onStartSlow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560)
                        )
                    ) {
                        Text("慢划", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onStartFast,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00B3B3)
                        )
                    ) {
                        Text("快划", fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF666666)
                    )
                ) {
                    Text("关闭", fontSize = 12.sp)
                }
            }
        }
    }
}
