package com.sensitivitysync.ui.theme

import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.utils.MiuixTheme

@Composable
fun NekoSenseTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MiuixTheme(
        colors = if (darkTheme) darkColors() else lightColors(),
        content = content
    )
}
