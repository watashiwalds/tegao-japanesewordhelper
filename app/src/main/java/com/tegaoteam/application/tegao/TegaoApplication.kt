package com.tegaoteam.application.tegao

import android.app.Application
import timber.log.Timber

class TegaoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        instance = this
    }

    companion object {
        lateinit var instance: Application
            private set
    }

}