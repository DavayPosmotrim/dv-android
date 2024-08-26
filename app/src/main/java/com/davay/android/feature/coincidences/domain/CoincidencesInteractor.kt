package com.davay.android.feature.coincidences.domain

interface CoincidencesInteractor {
    fun markFirstTimeLaunch()
    fun isFirstTimeLaunch(): Boolean
}