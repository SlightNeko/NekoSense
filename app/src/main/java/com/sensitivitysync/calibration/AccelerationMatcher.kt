package com.sensitivitysync.calibration

import com.sensitivitysync.data.AccelCompareResult
import com.sensitivitysync.data.AdjustDirection
import kotlin.math.abs

class AccelerationMatcher {

    private var matchThreshold: Float = 0.05f

    fun setThreshold(threshold: Float) {
        matchThreshold = threshold.coerceIn(0.01f, 0.10f)
    }

    fun compare(
        ratioA: Float,
        ratioB: Float
    ): AccelCompareResult {
        val error = abs(ratioA - ratioB) / ratioA
        val isMatched = error <= matchThreshold
        val direction = if (!isMatched) {
            if (ratioB > ratioA) AdjustDirection.DECREASE else AdjustDirection.INCREASE
        } else null

        return AccelCompareResult(
            ratioA = ratioA,
            ratioB = ratioB,
            error = error,
            isMatched = isMatched,
            direction = direction
        )
    }

    fun computeAccelRatio(
        slowDistancePx: Float,
        fastDistancePx: Float
    ): Float {
        if (fastDistancePx <= 0f) return 1f
        return slowDistancePx / fastDistancePx
    }

    fun formatSuggestion(accelValue: Int, direction: AdjustDirection): String {
        return when (direction) {
            AdjustDirection.INCREASE -> "Acceleration too low, increase to ~${accelValue + 10}"
            AdjustDirection.DECREASE -> "Acceleration too high, decrease to ~${accelValue - 10}"
        }
    }
}
