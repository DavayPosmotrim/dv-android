package com.davay.android.feature.createsession.presentation.createsession

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.davay.android.R
import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.impl.CommonWebsocketInteractor
import com.davay.android.core.domain.models.SessionShort
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

open class CreateSessionViewModel @Inject constructor(
    private val commonWebsocketInteractor: CommonWebsocketInteractor,
) : BaseViewModel() {

    protected fun subscribeToWebsocketsAndUpdateSessionId(sessionId: String, onFailure: () -> Unit) {
        viewModelScope.launch {
            runCatching {
                commonWebsocketInteractor.subscribeWebsockets(sessionId)
            }.onFailure {
                onFailure.invoke()
            }
        }
    }

    protected fun navigateToWaitSession(session: SessionShort) {
        val sessionJson = Json.encodeToString(session)
        val bundle = Bundle().apply {
            putString(SESSION_DATA, sessionJson)
        }
        navigate(
            R.id.action_createSessionFragment_to_waitSessionFragment,
            bundle
        )
    }

    companion object {
        const val SESSION_DATA = "session_data"
        val TAG = CreateSessionViewModel::class.simpleName
    }
}
