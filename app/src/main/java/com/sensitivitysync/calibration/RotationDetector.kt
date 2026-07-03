package com.sensitivitysync.calibration

import android.graphics.Bitmap
import kotlin.math.abs
import kotlin.math.sqrt

class RotationDetector {

    private var referenceColumns: List<FloatArray> = emptyList()
    private var templateWidth = 5
    private var isCalibrated = false
    private var isFastSwipeMode = false
    private val capturedFrames = mutableListOf<FrameData>()

    data class FrameData(
        val columns: List<FloatArray>,
        val timestampMs: Long,
        val swipeProgress: Float
    )

    fun captureReference(frame: Bitmap, centerX: Float = 0.5f) {
        referenceColumns = extractColumns(frame, centerX, templateWidth)
        isCalibrated = true
    }

    fun analyzeFrame(
        frame: Bitmap,
        swipeProgress: Float,
        centerX: Float = 0.5f
    ): AnalysisResult {
        if (!isCalibrated) return AnalysisResult(0f, false, 0f)

        val currentColumns = extractColumns(frame, centerX, templateWidth)
        val correlation = computeCorrelation(referenceColumns, currentColumns)

        return AnalysisResult(
            correlation = correlation,
            isMatch = correlation > matchThreshold,
            progress = swipeProgress
        )
    }

    fun startFastSwipeCapture(frame: Bitmap, centerX: Float) {
        isFastSwipeMode = true
        capturedFrames.clear()
        referenceColumns = extractColumns(frame, centerX, templateWidth)
        capturedFrames.add(
            FrameData(referenceColumns, System.currentTimeMillis(), 0f)
        )
    }

    fun recordFastFrame(frame: Bitmap, progress: Float, centerX: Float = 0.5f) {
        if (!isFastSwipeMode) return
        val cols = extractColumns(frame, centerX, templateWidth)
        capturedFrames.add(FrameData(cols, System.currentTimeMillis(), progress))
    }

    fun analyzeFastCapture(): AnalysisResult {
        isFastSwipeMode = false
        if (capturedFrames.size < 3) return AnalysisResult(0f, false, 0f)

        var bestMatchFrame: FrameData? = null
        var bestCorrelation = 0f

        for (i in 1 until capturedFrames.size) {
            val corr = computeCorrelation(referenceColumns, capturedFrames[i].columns)
            if (corr > bestCorrelation) {
                bestCorrelation = corr
                bestMatchFrame = capturedFrames[i]
            }
        }

        val matched = bestMatchFrame != null && bestCorrelation > matchThreshold
        return AnalysisResult(
            correlation = bestCorrelation,
            isMatch = matched,
            progress = bestMatchFrame?.swipeProgress ?: 1f
        )
    }

    private fun extractColumns(
        frame: Bitmap, centerXFraction: Float, width: Int
    ): List<FloatArray> {
        val columns = mutableListOf<FloatArray>()
        val startX = ((frame.width * centerXFraction) - width / 2).toInt().coerceIn(0, frame.width - width)
        val pixels = IntArray(width * frame.height)
        frame.getPixels(pixels, 0, width, startX, 0, width, frame.height)

        for (col in 0 until width) {
            val column = FloatArray(frame.height)
            for (row in 0 until frame.height) {
                val pixel = pixels[row * width + col]
                column[row] = pixelToLuminance(pixel)
            }
            columns.add(column)
        }
        return columns
    }

    private fun pixelToLuminance(pixel: Int): Float {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return 0.299f * r + 0.587f * g + 0.114f * b
    }

    private fun computeCorrelation(ref: List<FloatArray>, current: List<FloatArray>): Float {
        require(ref.size == current.size)
        val correlations = ref.zip(current).map { (r, c) ->
            columnCorrelation(r, c)
        }
        return correlations.median()
    }

    private fun columnCorrelation(a: FloatArray, b: FloatArray): Float {
        val n = a.size
        var sumA = 0f; var sumB = 0f
        var sumAA = 0f; var sumBB = 0f; var sumAB = 0f

        for (i in 0 until n) {
            sumA += a[i]; sumB += b[i]
            sumAA += a[i] * a[i]; sumBB += b[i] * b[i]; sumAB += a[i] * b[i]
        }

        val meanA = sumA / n; val meanB = sumB / n
        val varA = sumAA / n - meanA * meanA
        val varB = sumBB / n - meanB * meanB

        if (varA <= 0f || varB <= 0f) return 0f

        val cov = sumAB / n - meanA * meanB
        return (cov / sqrt(varA * varB)).coerceIn(-1f, 1f)
    }

    private fun List<Float>.median(): Float {
        if (isEmpty()) return 0f
        val sorted = sorted()
        val mid = size / 2
        return if (size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2f else sorted[mid]
    }

    fun reset() {
        isCalibrated = false
        isFastSwipeMode = false
        referenceColumns = emptyList()
        capturedFrames.clear()
    }

    var matchThreshold: Float = 0.85f

    data class AnalysisResult(
        val correlation: Float,
        val isMatch: Boolean,
        val progress: Float
    )
}
