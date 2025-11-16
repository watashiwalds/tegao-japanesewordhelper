package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.tegaoteam.application.tegao.R

object QuickPreset {
    fun requestConfirmation(context: Context, titleId: Any? = null, messageId: Any? = null, lambdaRun: (() -> Unit)? = null) {
        AlertDialog.Builder(context).apply {
            titleId?.let {
                if (it is String) setTitle(it)
                if (it is Int) setTitle(it)
            }
            messageId?.let {
                if (it is String) setTitle(it)
                if (it is Int) setTitle(it)
            }
            setPositiveButton(R.string.phrase_confirm, object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    lambdaRun?.invoke()
                }
            })
            setNegativeButton(R.string.phrase_cancel, null)
        }.show()
    }
}