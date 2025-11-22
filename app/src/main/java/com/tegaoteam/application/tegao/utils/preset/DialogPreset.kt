package com.tegaoteam.application.tegao.utils.preset

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.DialogYesOrNoBinding
import com.tegaoteam.application.tegao.utils.setTextWithResId

object DialogPreset {
    fun requestConfirmation(context: Context, title: Any? = null, message: Any? = null, lambdaRun: (() -> Unit)? = null) {
        val binding = inflateBareboneYesNoDialog(context, title, message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke(); dialog.dismiss() }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }
        binding.executePendingBindings()

        dialog.show()
    }
    fun requestValueDialog(context: Context, title: Any? = null, message: Any? = null, lambdaRun: ((String) -> Unit)? = null) {
        val binding = inflateBareboneYesNoDialog(context, title, message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        val editText = EditText(context)
        binding.frame.addView(editText)
        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke(editText.text.toString()); dialog.dismiss() }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }
        binding.executePendingBindings()

        dialog.show()
    }

    private fun inflateBareboneYesNoDialog(context: Context, title: Any? = null, message: Any? = null): DialogYesOrNoBinding {
        val binding = DataBindingUtil.inflate<DialogYesOrNoBinding>(LayoutInflater.from(context), R.layout.dialog_yes_or_no, null, false)
        when (title) {
            is String -> binding.label.text = title
            is Int -> binding.label.setTextWithResId(title)
            else -> binding.label.setTextWithResId(0)
        }
        when (message) {
            is String -> binding.message.text = message
            is Int -> binding.message.setTextWithResId(message)
            else -> binding.message.setTextWithResId(0)
        }
        return binding
    }
}