package com.tegaoteam.application.tegao.data.config

import android.content.pm.PackageManager
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.setting.SettingConfig
import timber.log.Timber

object AddonConfig {
    private const val handwritingPackagePath = "com.tegaoteam.addon.tegao.handwritingrecognition"

    private fun checkHandwritingAvailability(): Boolean {
        Timber.i("Check for appearance of Handwriting Recognition")
        val result = try {
            val info = TegaoApplication.instance.applicationContext.packageManager.getPackageInfo(
                handwritingPackagePath,
                0
            )
            Timber.i("Found Handwriting Recognition addon: $info")
            true
        } catch (_: PackageManager.NameNotFoundException) {
            Timber.i("Not found Handwriting Recognition addon")
            false
        }
        return result
    }

    val isHandwritingAvailable = checkHandwritingAvailability()
}