package com.davai.uikit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class ProgressBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }
    private var rotationAngle = 0f
    private val rectF = RectF()
    private var gradient: SweepGradient? = null

    init {
        ValueAnimator.ofFloat(0f, FULL_ROTATION).apply {
            duration = ANIMATION_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                rotationAngle = animation.animatedValue as Float
                invalidate()
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = Math.min(cx, cy) - paint.strokeWidth / 2

        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)

        if (gradient == null) {
            gradient = SweepGradient(cx, cy, GRADIENT_COLORS, GRADIENT_POSITIONS)
        }

        paint.shader = gradient

        canvas.save()
        canvas.rotate(rotationAngle, cx, cy)
        canvas.drawArc(rectF, 0f, FULL_ROTATION, false, paint)
        canvas.restore()
    }
    companion object {
        private const val ANIMATION_DURATION = 1400L
        private const val STROKE_WIDTH = 25f
        private val GRADIENT_COLORS = intArrayOf(Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE)
        private val GRADIENT_POSITIONS = floatArrayOf(0f, 0.2f, 0.4f, 1f)
        private const val FULL_ROTATION = 360f
    }
}
