package com.sensitivitysync.calibration

import kotlin.math.roundToInt

class SensitivityConverter {

    data class ConvertResult(
        val baseSensitivity: Float,
        val roundedValue: Float
    )

    fun convertBaseSensitivity(
        gameABaseSens: Float,
        gameASlowPx: Float,
        gameBSlowPx: Float
    ): ConvertResult {
        if (gameBSlowPx <= 0f) return ConvertResult(0f, 0f)
        val target = gameABaseSens * gameASlowPx / gameBSlowPx
        return ConvertResult(
            baseSensitivity = target,
            roundedValue = roundToGameValue(target)
        )
    }

    fun computeCmPer360(
        distancePx: Float,
        dpi: Float
    ): Float {
        if (dpi <= 0f || distancePx <= 0f) return 0f
        return (distancePx / dpi) * 2.54f
    }

    private fun roundToGameValue(value: Float): Float {
        val rounded = (value * 10).roundToInt() / 10f
        return if (rounded < 1f) (value * 100).roundToInt() / 100f else rounded
    }
}
