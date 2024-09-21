package com.davay.android.base

import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

sealed class NavigationCommand {
    class ToDirection(
        val directions: NavDirections,
        val navOptions: NavOptions? = null
    ) : NavigationCommand()

    object Back : NavigationCommand()
}