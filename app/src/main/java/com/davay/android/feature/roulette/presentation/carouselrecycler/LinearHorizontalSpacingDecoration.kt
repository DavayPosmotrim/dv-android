package com.davay.android.feature.roulette.presentation.carouselrecycler

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class LinearHorizontalSpacingDecoration(@Px private val innerSpacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        val itemWidth = view.layoutParams.width
        val offset = (parent.width - itemWidth) / 2

        outRect.left = if (itemPosition == 0) offset else innerSpacing / 2
        outRect.right = if (itemPosition == state.itemCount - 1) 0 else innerSpacing / 2
    }
}
