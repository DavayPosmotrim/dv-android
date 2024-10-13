package com.davay.android.feature.sessionlist.data.network

import com.davay.android.core.data.dto.ConnectMessageDto
import com.davay.android.core.data.dto.SessionDto

interface ConnectToSessionResponse {
    class ConnectToSession(val value: ConnectMessageDto) : ConnectToSessionResponse
    class Session(val value: SessionDto) : ConnectToSessionResponse
}