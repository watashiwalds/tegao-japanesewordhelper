package com.tegaoteam.application.tegao

import android.app.Application
import com.tegaoteam.application.tegao.utils.TermBank
import timber.log.Timber

class TegaoApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        instance = this
        TermBank.init(this)
    }

    companion object {
        lateinit var instance: Application
            private set
    }

}