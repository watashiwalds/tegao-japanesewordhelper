package com.tegaoteam.application.tegao.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.tegaoteam.application.tegao.R

object QuickPreset {
    fun requestConfirmation(context: Context, title: Any? = null, message: Any? = null, lambdaRun: (() -> Unit)? = null) {
        AlertDialog.Builder(context).apply {
            title?.let {
                if (it is String) setTitle(it)
                if (it is Int) setTitle(it)
            }
            message?.let {
                if (it is String) setMessage(it)
                if (it is Int) setMessage(it)
            }
            setPositiveButton(R.string.phrase_confirm) { p0, p1 -> lambdaRun?.invoke() }
            setNegativeButton(R.string.phrase_cancel, null)
        }.show()
    }
}