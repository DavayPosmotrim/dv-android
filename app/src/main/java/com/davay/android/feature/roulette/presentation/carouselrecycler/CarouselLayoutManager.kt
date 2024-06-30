package com.davay.android.feature.roulette.presentation.carouselrecycler

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class CarouselLayoutManager(
    private val context: Context,
    private val minScaleDistanceFactor: Float = 2.0f,
    private val scaleDownBy: Float = 0.25f
) : LinearLayoutManager(context, HORIZONTAL, false) {

    private var speed: Float = SPEED_LOW

    override fun onLayoutCompleted(state: RecyclerView.State?) =
        super.onLayoutCompleted(state).also { applyScaleAndTranslationToChildren() }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) = super.scrollHorizontallyBy(dx, recycler, state)
        .also { applyScaleAndTranslationToChildren() }

    private fun applyScaleAndTranslationToChildren() {
        val containerCenter = width / 2f
        val scaleDistanceThreshold = minScaleDistanceFactor * containerCenter
        var translationXForward = 0f

        for (i in 0 until childCount) {
            getChildAt(i)?.let { child ->
                val childCenter = (child.left + child.right) / 2f
                val distanceToCenter = abs(childCenter - containerCenter)

                val scaleDownAmount = (distanceToCenter / scaleDistanceThreshold).coerceAtMost(1f)
                val scale = 1f - scaleDownBy * scaleDownAmount

                child.scaleX = scale
                child.scaleY = scale

                val translationDirection = if (childCenter > containerCenter) -1 else 1
                val translationXFromScale = translationDirection * child.width * (1 - scale) / 2f
                child.translationX = translationXFromScale + translationXForward
                translationXForward = 0f

                if (translationXFromScale > 0 && i >= 1) {
                    getChildAt(i - 1)!!.translationX += 2 * translationXFromScale
                } else if (translationXFromScale < 0) {
                    translationXForward = 2 * translationXFromScale
                }
            }
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val smoothScroller: LinearSmoothScroller = object :
            LinearSmoothScroller(context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return speed / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    fun setSlowSpeedTransition() {
        speed = SPEED_LOW
    }

    fun setFastSpeedTransition() {
        speed = SPEED_HIGH
    }

    companion object {
        private const val SPEED_LOW = 1500f
        private const val SPEED_HIGH = 100f
    }
}
