package com.tegaoteam.application.tegao.data.config

import android.content.pm.PackageManager
import com.tegaoteam.application.tegao.TegaoApplication
import timber.log.Timber

object AddonConfig {
    //region handwriting addon
    const val handwritingPackagePath = "com.tegaoteam.addon.tegao.handwritingrecognition"

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
    //endregion

    //region offline dictionary addon
    const val offlineDictionaryPackagePath = "com.tegaoteam.addon.tegao.yomitandictionary"

    private fun checkOfflineDictionaryAvailability(): Boolean {
        Timber.i("Check for appearance of Offline(Yomitan) Dictionary")
        val result = try {
            val info = TegaoApplication.instance.applicationContext.packageManager.getPackageInfo(
                offlineDictionaryPackagePath,
                0
            )
            Timber.i("Found Offline(Yomitan) Dictionary addon: $info")
            true
        } catch (_: PackageManager.NameNotFoundException) {
            Timber.i("Not found Offline(Yomitan) Dictionary addon")
            false
        }
        return result
    }

    val isOfflineDictionaryAvailable = checkOfflineDictionaryAvailability()
    //endregion
}