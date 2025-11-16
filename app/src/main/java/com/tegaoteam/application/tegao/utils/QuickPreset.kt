package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.tegaoteam.application.tegao.R

object QuickPreset {
    fun requestConfirmation(context: Context, titleId: Int? = null, messageId: Int? = null, lambdaRun: (() -> Unit)? = null) {
        AlertDialog.Builder(context).apply {
            titleId?.let { setTitle(it) }
            messageId?.let { setMessage(it) }
            setPositiveButton(R.string.phrase_confirm, object: DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    lambdaRun?.invoke()
                }
            })
            setNegativeButton(R.string.phrase_cancel, null)
        }.show()
    }
}