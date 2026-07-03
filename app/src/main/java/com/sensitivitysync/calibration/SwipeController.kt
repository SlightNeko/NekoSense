package com.sensitivitysync.calibration

import com.sensitivitysync.shizuku.ShizukuManager
import kotlinx.coroutines.delay

class SwipeController(
    private val shizuku: ShizukuManager
) {
    private var screenWidth = 1080
    private var screenHeight = 1920

    fun setScreenSize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    suspend fun swipeSlow(onProgress: (Float) -> Unit = {}): Boolean {
        return swipe(
            startX = screenWidth * 0.2f,
            startY = screenHeight * 0.5f,
            endX = screenWidth * 0.8f,
            endY = screenHeight * 0.5f,
            durationMs = SLOW_DURATION_MS,
            onProgress = onProgress
        )
    }

    suspend fun swipeFast(onProgress: (Float) -> Unit = {}): Boolean {
        return swipe(
            startX = screenWidth * 0.2f,
            startY = screenHeight * 0.5f,
            endX = screenWidth * 0.8f,
            endY = screenHeight * 0.5f,
            durationMs = FAST_DURATION_MS,
            onProgress = onProgress
        )
    }

    suspend fun swipeFullScreen(
        direction: SwipeDirection = SwipeDirection.LEFT,
        durationMs: Int = SLOW_DURATION_MS,
        onProgress: (Float) -> Unit = {}
    ): Boolean {
        val (x1, x2) = when (direction) {
            SwipeDirection.LEFT -> Pair(screenWidth * 0.9f, screenWidth * 0.1f)
            SwipeDirection.RIGHT -> Pair(screenWidth * 0.1f, screenWidth * 0.9f)
        }
        return swipe(
            startX = x1, startY = screenHeight * 0.5f,
            endX = x2, endY = screenHeight * 0.5f,
            durationMs = durationMs,
            onProgress = onProgress
        )
    }

    private suspend fun swipe(
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        durationMs: Int,
        onProgress: (Float) -> Unit = {}
    ): Boolean {
        val result = shizuku.inputSwipe(
            startX.toInt(), startY.toInt(),
            endX.toInt(), endY.toInt(),
            durationMs
        )
        if (!result.isSuccess) return false

        val steps = 10
        for (i in 0 until steps) {
            delay(durationMs.toLong() / steps)
            onProgress((i + 1).toFloat() / steps)
        }
        return true
    }

    suspend fun getPointerLocation(): PointerInfo? {
        val result = shizuku.executeCommand("getevent -lp 2>/dev/null | grep -i 'touch\\|pointer'")
        return null
    }

    enum class SwipeDirection { LEFT, RIGHT }

    data class PointerInfo(
        val device: String,
        val minX: Int, val maxX: Int,
        val minY: Int, val maxY: Int
    )

    companion object {
        const val SLOW_DURATION_MS = 1500
        const val FAST_DURATION_MS = 200
    }
}
