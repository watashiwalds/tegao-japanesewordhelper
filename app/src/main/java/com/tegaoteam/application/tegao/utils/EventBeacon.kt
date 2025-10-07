package com.tegaoteam.application.tegao.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EventBeacon {
    private val _beacon = MutableLiveData<Boolean>()
    val beacon: LiveData<Boolean> = _beacon
    fun beaconOn() { _beacon.value = true }
    fun beaconOff() { _beacon.value = false }
}