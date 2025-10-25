package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.widget.Toast
import com.tegaoteam.application.tegao.TegaoApplication

class AppToast {
    @Suppress("unused")
    companion object {
        const val LENGTH_SHORT = 0
        const val LENGTH_LONG = 1

        private var toastInstance = Toast(TegaoApplication.instance.applicationContext)

        fun show(text: CharSequence, duration: Int, context: Context = TegaoApplication.instance.applicationContext) {
            toastInstance.cancel()
            toastInstance = Toast.makeText(context, text, duration)
            toastInstance.show()
        }
    }
}