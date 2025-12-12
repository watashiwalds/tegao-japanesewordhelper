package com.tegaoteam.application.tegao.ui.shared.preset

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.DialogProcessingBinding
import com.tegaoteam.application.tegao.databinding.DialogQuickViewBinding
import com.tegaoteam.application.tegao.databinding.DialogYesOrNoBinding
import com.tegaoteam.application.tegao.utils.setTextWithResId
import com.tegaoteam.application.tegao.utils.toggleVisibility

object DialogPreset {
    private var processingDialog: AlertDialog? = null
    fun cancelCurrentProcessingDialog() {
        processingDialog?.cancel()
        processingDialog = null
    }
    fun processing(context: Context, message: Any? = null, cancelLambda: (() -> Unit)? = null): TextView {
        val binding = DataBindingUtil.inflate<DialogProcessingBinding>(LayoutInflater.from(context), R.layout.dialog_processing, null ,false)
        handleSetText(message, binding.message)

        processingDialog?.cancel()
        processingDialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
            setCancelable(false)
        }.create()

        cancelLambda?.let {
            binding.cancelBtn.setOnClickListener { cancelLambda.invoke(); processingDialog?.cancel() }
        }?: {
            binding.cancelBtn.toggleVisibility(false)
        }

        binding.executePendingBindings()

        processingDialog?.show()
        return binding.progress
    }

    fun requestConfirmation(context: Context, title: Any? = null, message: Any? = null, lambdaRun: (() -> Unit)? = null) {
        val binding = DataBindingUtil.inflate<DialogYesOrNoBinding>(LayoutInflater.from(context), R.layout.dialog_yes_or_no, null, false)
        handleSetText(title, binding.label)
        handleSetText(message, binding.message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke(); dialog.dismiss() }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }
        binding.executePendingBindings()

        dialog.show()
    }
    fun requestValueDialog(context: Context, title: Any? = null, message: Any? = null, defaultValue: String? = null, lambdaRun: ((String) -> Unit)? = null) {
        val binding = DataBindingUtil.inflate<DialogYesOrNoBinding>(LayoutInflater.from(context), R.layout.dialog_yes_or_no, null, false)
        handleSetText(title, binding.label)
        handleSetText(message, binding.message)

        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()

        val editText = EditText(context).apply { editableText.append(defaultValue) }
        binding.frame.addView(editText)
        binding.confirmBtn.setOnClickListener { lambdaRun?.invoke(editText.text.toString()); dialog.dismiss() }
        binding.cancelBtn.setOnClickListener { dialog.dismiss() }
        binding.executePendingBindings()

        dialog.show()
    }

    fun quickView(context: Context, view: View, message: Any? = null, ratioHbyW: String? = null) {
        val binding = DataBindingUtil.inflate<DialogQuickViewBinding>(LayoutInflater.from(context), R.layout.dialog_quick_view, null, false)
        ratioHbyW?.let {
            val plc = binding.frame.layoutParams as ConstraintLayout.LayoutParams
            plc.height = 0
            plc.dimensionRatio = ratioHbyW
            binding.frame.apply {
                layoutParams = plc
                requestLayout()
            }
        }
        handleSetText(message, binding.message)
        val dialog = AlertDialog.Builder(context).apply {
            setView(binding.root)
        }.create()
        binding.apply {
            frame.apply {
                removeAllViews()
                (view.parent as? ViewGroup?)?.removeView(view)
                addView(view)
            }
            dismissBtn.setOnClickListener { dialog.dismiss() }
            view.findViewById<Button>(R.id.cancel_btn)?.setOnClickListener { dialog.dismiss() }
            executePendingBindings()
        }
        dialog.show()
    }

    private var _globalSnackbar: Snackbar? = null
    fun dismissCurrentSnackbar() {
        _globalSnackbar?.dismiss()
    }
    fun showSnackbar(view: View, message: Any) {
        _globalSnackbar = when (message) {
            is String -> Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply { show() }
            is Int -> Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).apply { show() }
            else -> null
        }
    }

    private fun handleSetText(value: Any?, textView: TextView) {
        when (value) {
            is String -> textView.text = value
            is Int -> textView.setTextWithResId(value)
            else -> textView.setTextWithResId(0)
        }
    }
}