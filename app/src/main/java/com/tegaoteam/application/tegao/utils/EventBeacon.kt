package com.tegaoteam.application.tegao.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Reducing ViewModel and Observer boilerplate when observing event
 *
 * [ignite] Ignite beacon on event
 *
 * [receive] Receiving the event and turn off the beacon in the process
 *
 * @property beacon LiveData to observe the event
 */
class EventBeacon {
    private val _beacon = MutableLiveData<Boolean>()
    private var _message: String? = null

    /**
     * Watch event state with this LiveData
     */
    val beacon: LiveData<Boolean> = _beacon

    /**
     * Ignite the event beacon to abort those who observe
     *
     * @return Nothing
     */
    fun ignite() { _beacon.value = true }
    fun ignite(message: String) {
        _message = message
        _beacon.value = true
    }

    /**
     * Receive the event and turn off the beacon
     * After successfully received the event, beacon would turn off
     *
     * @return ```=true``` if received, ```=false``` in reverse
     */
    fun receive(): Boolean {
        return if (beacon.value!!) {
            _beacon.value = false
            true
        } else false
    }

    /**
     * Get message that being sent with the beacon ignite
     * Self-destruct after fetch
     */
    fun getMessage(): String? {
        val msg = _message
        _message = null
        return msg
    }
}