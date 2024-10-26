package com.davay.android.core.data.network.model.getsession

import com.davay.android.core.data.dto.SessionResultDto

sealed interface GetSessionResponse {
    class Session(val value: SessionResultDto) : GetSessionResponse
}