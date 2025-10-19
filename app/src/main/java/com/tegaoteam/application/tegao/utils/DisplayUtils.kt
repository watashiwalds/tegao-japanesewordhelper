package com.tegaoteam.application.tegao.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("toggleVisibility")
fun View.toggleVisibility(allowShow: Boolean) {
    visibility = if (allowShow) View.VISIBLE else View.GONE
}

@BindingAdapter("goneWhenEmpty")
fun TextView.goneWhenEmpty(enable: Boolean) {
    if (enable && text.toString().isBlank()) visibility = View.GONE
}