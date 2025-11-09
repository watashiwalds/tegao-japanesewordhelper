package com.tegaoteam.application.tegao.data.config

import android.content.pm.PackageManager
import com.tegaoteam.application.tegao.TegaoApplication

object AddonConfig {
    private fun checkHandwritingAvailability(): Boolean {
        val result = try {
            TegaoApplication.instance.applicationContext.packageManager.getPackageInfo(
                "com.tegaoteam.addon.tegao.handwritingrecognition",
                0
            )
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
//        return result
        return true
    }

    val isHandwritingAvailable = checkHandwritingAvailability()
}