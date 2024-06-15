package com.davai.uikit.progressbutton.progressapi

import android.graphics.Canvas

interface ProgressDrawer {
    fun drawCornerArc(
        canvas: Canvas,
        remainingLength: Float,
        cornerType: CornerType
    ): Float

    fun drawFirstHalfTopHorizontalLine(
        canvas: Canvas,
        halfLength: Float,
        currentLength: Float
    ): Float

    fun drawRightVerticalLine(canvas: Canvas, remainingLength: Float): Float
    fun drawBottomLine(canvas: Canvas, remainingLength: Float): Float
    fun drawLeftVerticalLine(canvas: Canvas, remainingLength: Float): Float
    fun drawSecondHalfTopProgressLine(canvas: Canvas, remainingLength: Float)
    fun updateProgress(newProgres: Int)
    companion object {
        const val ANGLE_0_DEG = 0f
        const val ANGLE_90_DEG = 90f
        const val ANGLE_180_DEG = 180f
        const val ANGLE_270_DEG = 270f
        const val MS_IN_SECOND = 1000L
        const val DEFAULT_DURATION_5000_MS = 5000L
    }
}