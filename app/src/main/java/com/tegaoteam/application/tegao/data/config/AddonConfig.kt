package com.tegaoteam.application.tegao.data.config

object AddonConfig {
    private fun handwritingModuleAvailability(): Boolean {
        //todo: Research PackageManager to check for com.tegaoteam.tegao.addon.handwritingrecognition
        //temporal enable for WritingView testing
        return true
    }

    val isHandwritingAvailable = handwritingModuleAvailability()
}