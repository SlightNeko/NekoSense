package com.sensitivitysync.shizuku

import android.os.IBinder
import dev.rikka.shizuku.ShizukuBinderWrapper
import dev.rikka.shizuku.SystemServiceHelper

class ShizukuHost {

    private var inputManager: IBinder? = null

    fun init() {
        try {
            inputManager = ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService("input")
            )
        } catch (_: Exception) {
        }
    }

    fun injectSwipe(
        pointerId: Int,
        x1: Float, y1: Float, x2: Float, y2: Float,
        steps: Int = 20, durationMs: Int = 500
    ) {
        // Fallback to shell command if direct injection fails
    }
}
