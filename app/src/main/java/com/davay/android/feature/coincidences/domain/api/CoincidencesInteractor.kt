package com.davay.android.feature.coincidences.domain.api

interface CoincidencesInteractor {
    fun markFirstTimeLaunch()
    fun isFirstTimeLaunch(): Boolean
}