package com.davay.android.feature.createsession.presentation.createsession

import com.davay.android.base.BaseViewModel
import com.davay.android.core.domain.models.Session
import javax.inject.Inject

open class CreateSessionViewModel @Inject constructor() : BaseViewModel() {

    protected fun navigateToWaitSession(session: Session) {
        val action = CreateSessionFragmentDirections
            .actionCreateSessionFragmentToWaitSessionFragment(session)
        navigate(action)
    }
}
