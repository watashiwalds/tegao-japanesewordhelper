package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.DialogYesOrNoBinding

object QuickPreset {
    fun requestConfirmation(context: Context, title: Any? = null, message: Any? = null, lambdaRun: (() -> Unit)? = null) {
        val binding = inflateBareboneYesNoDialog(context, title, message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke() }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
    fun requestValueDialog(context: Context, title: Any? = null, message: Any? = null, lambdaRun: ((String) -> Unit)? = null) {
        val binding = inflateBareboneYesNoDialog(context, title, message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        val editText = EditText(context)
        binding.frame.addView(editText)
        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke(editText.text.toString()) }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }
        binding.executePendingBindings()

        dialog.show()
    }

    private fun inflateBareboneYesNoDialog(context: Context, title: Any? = null, message: Any? = null): DialogYesOrNoBinding {
        val binding = DataBindingUtil.inflate<DialogYesOrNoBinding>(LayoutInflater.from(context), R.layout.dialog_yes_or_no, null, false)
        title?.let {
            if (it is String) binding.label.text = it
            if (it is Int) binding.label.setText(it)
        }
        message?.let {
            if (it is String) binding.message.text = it
            if (it is Int) binding.message.setText(it)
        }
        return binding
    }
}