package com.davay.android.feature.coincidences.data.network.models

import com.davay.android.core.data.dto.SessionDto

sealed interface GetSessionResponse {
    class Session(val value: SessionDto) : GetSessionResponse
}