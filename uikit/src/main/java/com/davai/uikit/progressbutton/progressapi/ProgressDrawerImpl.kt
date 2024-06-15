package com.davai.uikit.progressbutton.progressapi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.display.DisplayManager
import android.view.Display
import com.davai.uikit.progressbutton.ProgressButtonView
import kotlin.math.min

class ProgressDrawerImpl(private val view: ProgressButtonView) : ProgressDrawer {
    private var progress = 0
    private var numOfSteps: Int = 0

    private val height = view.height
    private val width = view.width
    private val cornerRadius = view.cornerRadius.toFloat()
    private val progressStrokeWidth = view.getProgressStrokeWidth()
    private val paint = Paint().apply {
        color = view.getProgressStrokeColor()
        strokeWidth = view.getProgressStrokeWidth()
    }
    private val arcPaint = Paint().apply {
        color = view.getProgressStrokeColor()
        strokeWidth = view.getProgressStrokeWidth()
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    override fun updateProgress(newProgres: Int) {
        progress = newProgres
    }

    override fun drawCornerArc(
        canvas: Canvas,
        remainingLength: Float,
        cornerType: CornerType
    ): Float {
        val cornerLength = Math.PI.toFloat() * cornerRadius / 2 - progress
        val sweepAngle = min(
            remainingLength / cornerLength * ProgressDrawer.ANGLE_90_DEG,
            ProgressDrawer.ANGLE_90_DEG
        )
        val left =
            if (cornerType == CornerType.TOP_RIGHT || cornerType == CornerType.BOTTOM_RIGHT) {
                width - cornerRadius - progressStrokeWidth
            } else {
                2f
            }
        val top =
            if (cornerType == CornerType.BOTTOM_RIGHT || cornerType == CornerType.BOTTOM_LEFT) {
                height - cornerRadius - progressStrokeWidth
            } else {
                2f
            }
        val right =
            if (cornerType == CornerType.TOP_RIGHT || cornerType == CornerType.BOTTOM_RIGHT) {
                width - 2f
            } else {
                cornerRadius + 2
            }
        val bottom =
            if (cornerType == CornerType.BOTTOM_RIGHT || cornerType == CornerType.BOTTOM_LEFT) {
                height - 2f
            } else {
                cornerRadius + 2
            }
        canvas.drawArc(
            left,
            top,
            right,
            bottom,
            cornerType.startAngle,
            sweepAngle,
            false,
            arcPaint
        )
        return remainingLength - cornerLength
    }

    override fun drawFirstHalfTopHorizontalLine(
        canvas: Canvas,
        halfLength: Float,
        currentLength: Float
    ): Float {
        val lineLength = width - halfLength - cornerRadius / 2
        val drawLength = min(currentLength, lineLength)

        canvas.drawLine(
            halfLength,
            0f,
            min(halfLength + currentLength, lineLength),
            0f,
            paint
        )

        return currentLength - drawLength
    }

    override fun drawRightVerticalLine(canvas: Canvas, remainingLength: Float): Float {
        val lineLength = height - cornerRadius
        val drawLength = min(remainingLength, lineLength)
        canvas.drawLine(
            width.toFloat(),
            cornerRadius / 2,
            width.toFloat(),
            cornerRadius / 2 + drawLength,
            paint
        )
        return remainingLength - drawLength
    }

    override fun drawBottomLine(canvas: Canvas, remainingLength: Float): Float {
        val bottomLineStartX = canvas.width - cornerRadius / 2
        val bottomLineLength = bottomLineStartX - cornerRadius / 2
        val drawLength = min(remainingLength, bottomLineLength)

        canvas.drawLine(
            bottomLineStartX,
            canvas.height.toFloat(),
            bottomLineStartX - drawLength,
            canvas.height.toFloat(),
            paint
        )

        return remainingLength - drawLength
    }

    override fun drawLeftVerticalLine(canvas: Canvas, remainingLength: Float): Float {
        val lineLength = height - cornerRadius
        val drawLength = min(remainingLength, lineLength)
        canvas.drawLine(
            0f,
            height.toFloat() - cornerRadius / 2,
            0f,
            height.toFloat() - cornerRadius / 2 - drawLength,
            paint
        )
        return remainingLength - drawLength
    }

    override fun drawSecondHalfTopProgressLine(canvas: Canvas, remainingLength: Float) {
        val lineLength = width / 2 - cornerRadius / 2
        val drawLength = min(remainingLength, lineLength)

        canvas.drawLine(
            cornerRadius / 2,
            0f,
            cornerRadius / 2 + drawLength,
            0f,
            paint
        )
    }

    private fun calculateTotalPathLength(strokeCornerRadius: Float): Float {
        return 2 * (width + height - strokeCornerRadius + Math.PI.toFloat() * strokeCornerRadius)
    }

    private fun calculateCurrentLength(totalPathLength: Float): Float {
        return totalPathLength * progress / numOfSteps
    }

    private fun getDisplayRefreshDelay(): Long {
        val displayManger = view.context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManger.getDisplay(Display.DEFAULT_DISPLAY)
        return (ProgressButtonView.MS_IN_SECOND / display.refreshRate).toLong()
    }
}