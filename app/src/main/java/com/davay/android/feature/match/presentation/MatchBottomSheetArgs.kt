package com.davay.android.feature.match.presentation

import android.os.Parcelable
import com.davay.android.core.domain.models.MovieDetails
import kotlinx.parcelize.Parcelize

@Parcelize
class MatchBottomSheetArgs(
    val movieDetails: MovieDetails,
    val buttonText: String? = null,
    val showDismisAnimation: Boolean = true,
    val action: (() -> Unit)? = null
) : Parcelable