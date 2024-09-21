package com.davay.android.feature.onboarding.presentation

import android.os.Parcelable
import androidx.navigation.NavDirections
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class OnboardingItem(
    val textResId: Int? = null,
    val imageResId: Int? = null,
    val descriptionResId: Int? = null,
    val action: @RawValue NavDirections? = null
) : Parcelable