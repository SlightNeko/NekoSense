package com.sensitivitysync.data

enum class CalibrationStep {
    IDLE,
    PERMISSIONS_REQUIRED,
    GAME_A_INPUT,
    GAME_A_SLOW_READY,
    GAME_A_SLIDING,
    GAME_A_SLOW_DONE,
    GAME_A_FAST_READY,
    GAME_A_FAST_SLIDING,
    GAME_A_FAST_DONE,
    GAME_B_INPUT,
    GAME_B_ACCEL_READY,
    GAME_B_SLOW_READY,
    GAME_B_SLIDING,
    GAME_B_SLOW_DONE,
    GAME_B_FAST_READY,
    GAME_B_FAST_SLIDING,
    GAME_B_FAST_DONE,
    ACCEL_COMPARE,
    ACCEL_ADJUST,
    BASE_CONVERT,
    VERIFY
}

data class CalibrationSession(
    val step: CalibrationStep = CalibrationStep.IDLE,
    val gameAName: String = "",
    val baseSensA: Float = 0f,
    val accelA: Int = 0,
    val slowPxA: Float = 0f,
    val fastPxA: Float = 0f,
    val ratioA: Float = 0f,
    val gameBName: String = "",
    val accelB: Int = 0,
    val slowPxB: Float = 0f,
    val fastPxB: Float = 0f,
    val ratioB: Float = 0f,
    val matchThreshold: Float = 0.05f,
    val suggestion: String = "",
    val resultBaseSensB: Float = 0f,
    val resultAccelB: Int = 0,
    val iterationCount: Int = 0
) {
    val isComplete: Boolean get() = step == CalibrationStep.BASE_CONVERT

    fun reset(): CalibrationSession = CalibrationSession()
}

enum class SwipeSpeed {
    SLOW,
    FAST
}

data class SwipeResult(
    val distancePx: Float,
    val speed: SwipeSpeed
)

data class AccelCompareResult(
    val ratioA: Float,
    val ratioB: Float,
    val error: Float,
    val isMatched: Boolean,
    val direction: AdjustDirection?
)

enum class AdjustDirection {
    INCREASE,
    DECREASE
}
