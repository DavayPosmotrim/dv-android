package com.davai.uikit.progressbutton.progressapi

enum class CornerType(val startAngle: Float) {
    TOP_RIGHT(ProgressDrawer.ANGLE_270_DEG),
    BOTTOM_RIGHT(ProgressDrawer.ANGLE_0_DEG),
    BOTTOM_LEFT(ProgressDrawer.ANGLE_90_DEG),
    TOP_LEFT(ProgressDrawer.ANGLE_180_DEG)
}