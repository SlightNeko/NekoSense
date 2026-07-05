package com.sensitivitysync.calibration

import com.sensitivitysync.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalibrationEngine(
    private val swipeController: SwipeController,
    private val rotationDetector: RotationDetector,
    private val accelMatcher: AccelerationMatcher,
    private val converter: SensitivityConverter
) {
    private val _session = MutableStateFlow(CalibrationSession())
    val session: StateFlow<CalibrationSession> = _session.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    fun setStatus(msg: String) {
        _status.value = msg
    }

    fun startNewSession() {
        _session.value = CalibrationSession(step = CalibrationStep.GAME_A_INPUT)
        _status.value = "Enter Game A settings"
    }

    fun setGameAInput(baseSens: Float, accel: Int) {
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_A_SLOW_READY,
            baseSensA = baseSens,
            accelA = accel
        )
        _status.value = "Switch to Game A, then tap Slow Calibrate"
    }

    fun onSlowSwipeAComplete(distancePx: Float) {
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_A_SLOW_DONE,
            slowPxA = distancePx
        )
        _status.value = "Game A slow done (${"%.0f".format(distancePx)}px). Tap Fast Calibrate"
    }

    fun onFastSwipeAComplete(distancePx: Float) {
        val ratioA = accelMatcher.computeAccelRatio(
            _session.value.slowPxA, distancePx
        )
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_A_FAST_DONE,
            fastPxA = distancePx,
            ratioA = ratioA
        )
        _status.value = "Game A: ratio ${"%.2f".format(ratioA)}x. Now set Game B accel."
    }

    fun setGameBAccel(accel: Int) {
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_B_SLOW_READY,
            accelB = accel
        )
        _status.value = "Switch to Game B, tap Slow Calibrate"
    }

    fun onSlowSwipeBComplete(distancePx: Float) {
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_B_SLOW_DONE,
            slowPxB = distancePx
        )
        _status.value = "Game B slow done (${"%.0f".format(distancePx)}px). Tap Fast Calibrate"
    }

    fun onFastSwipeBComplete(distancePx: Float) {
        val ratioB = accelMatcher.computeAccelRatio(
            _session.value.slowPxB, distancePx
        )
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_B_FAST_DONE,
            fastPxB = distancePx,
            ratioB = ratioB
        )
        _status.value = "Game B: ratio ${"%.2f".format(ratioB)}x. Comparing..."
        compareAndConvert()
    }

    private fun compareAndConvert() {
        val s = _session.value
        val result = accelMatcher.compare(s.ratioA, s.ratioB)

        if (result.isMatched) {
            val conversion = converter.convertBaseSensitivity(
                s.baseSensA, s.slowPxA, s.slowPxB
            )
            _session.value = s.copy(
                step = CalibrationStep.BASE_CONVERT,
                resultBaseSensB = conversion.roundedValue,
                resultAccelB = s.accelB
            )
            _status.value = """
                ✓ Accelerations matched! (${"%.1f".format(s.ratioA)}x ≈ ${"%.1f".format(s.ratioB)}x)
                Game B → Base: ${conversion.roundedValue}, Accel: ${s.accelB}
            """.trimIndent()
        } else {
            val dir = when (result.direction) {
                AdjustDirection.INCREASE -> "increase"
                AdjustDirection.DECREASE -> "decrease"
                null -> "adjust"
            }
            _session.value = s.copy(
                step = CalibrationStep.ACCEL_ADJUST,
                suggestion = "Game B accel ratio ${"%.2f".format(s.ratioB)}x vs target ${"%.2f".format(s.ratioA)}x. Please $dir Game B acceleration.",
                iterationCount = s.iterationCount + 1
            )
            _status.value = _session.value.suggestion
        }
    }

    fun setMatchThreshold(threshold: Float) {
        accelMatcher.setThreshold(threshold)
        _session.value = _session.value.copy(matchThreshold = threshold)
    }

    fun retryGameB() {
        _session.value = _session.value.copy(
            step = CalibrationStep.GAME_B_ACCEL_READY,
            slowPxB = 0f, fastPxB = 0f, ratioB = 0f
        )
        _status.value = "Adjust Game B acceleration value and tap Confirm"
    }

    fun reset() {
        _session.value = CalibrationSession()
        _status.value = ""
        rotationDetector.reset()
    }

}
