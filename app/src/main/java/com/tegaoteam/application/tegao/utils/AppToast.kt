package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.widget.Toast
import com.tegaoteam.application.tegao.TegaoApplication
import timber.log.Timber

class AppToast {
    @Suppress("unused")
    companion object {
        const val LENGTH_SHORT = Toast.LENGTH_SHORT
        const val LENGTH_LONG = Toast.LENGTH_LONG

        private var toastInstance = Toast(TegaoApplication.instance.applicationContext)

        fun show(message: Any, duration: Int, context: Context = TegaoApplication.instance.applicationContext) {
            Timber.d("Toasting $message | ${message is Int} ${message is String}")
            toastInstance.cancel()
            when (message) {
                is Int -> toastInstance = Toast.makeText(context, message, duration)
                is String -> toastInstance = Toast.makeText(context, message, duration)
                is CharSequence -> toastInstance = Toast.makeText(context, message, duration)
                else -> toastInstance = Toast.makeText(context, "Toast Error", Toast.LENGTH_SHORT)
            }
            toastInstance.show()
        }
    }
}