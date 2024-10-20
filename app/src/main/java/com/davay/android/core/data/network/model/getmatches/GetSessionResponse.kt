package com.davay.android.core.data.network.model.getmatches

import com.davay.android.core.data.dto.MovieDto

sealed interface GetSessionResponse {
    class Session(val value: List<MovieDto>) : GetSessionResponse
}