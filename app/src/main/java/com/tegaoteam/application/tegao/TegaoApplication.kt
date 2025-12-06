package com.tegaoteam.application.tegao

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class TegaoApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        instance = this
    }

    companion object {
        val appIOScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        lateinit var instance: Application
            private set
    }

}