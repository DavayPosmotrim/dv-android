package com.davay.android.feature.coincidences.domain

import com.davay.android.core.domain.lounchcontrol.api.FirstTimeFlagRepository

class CoincidencesInteractorImpl(
    private val repository: FirstTimeFlagRepository
) : CoincidencesInteractor {
    override fun markFirstTimeLaunch() {
        repository.markFirstTimeLaunch()
    }

    override fun isFirstTimeLaunch(): Boolean {
        return repository.isFirstTimeLaunch()
    }
}