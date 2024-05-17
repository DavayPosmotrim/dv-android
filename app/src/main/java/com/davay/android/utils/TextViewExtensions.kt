package com.davay.android.utils

import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.davay.android.R
import com.davay.android.feature.splash.fragment.SplashFragment
import kotlin.random.Random

fun TextView.setSplashTextViewStyle() {
    background = ContextCompat.getDrawable(context, R.drawable.splash_item_background)
    setPaddingRelative(
        resources.getDimensionPixelSize(com.davai.uikit.R.dimen.padding_16),
        resources.getDimensionPixelSize(com.davai.uikit.R.dimen.padding_8),
        resources.getDimensionPixelSize(com.davai.uikit.R.dimen.padding_16),
        resources.getDimensionPixelSize(com.davai.uikit.R.dimen.padding_8)
    )
    setTextAppearance(R.style.Text_Base_SplashItem)
    rotation = Random.nextInt(
        SplashFragment.MAX_NEGATIVE_ANGLE_3000,
        SplashFragment.MAX_ANGLE_3000
    )
        .toFloat()
    layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        gravity = getRandomGravity()
        setMargins(
            0,
            resources.getDimensionPixelSize(com.davai.uikit.R.dimen.margin_negative_24),
            0,
            0
        )
    }
}

private fun getRandomGravity(): Int {
    return when (Random.nextInt(SplashFragment.START_POSITIONS_NUMBER)) {
        0 -> Gravity.TOP or Gravity.END
        1 -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
        else -> Gravity.TOP or Gravity.START
    }
}
