package com.tegaoteam.application.tegao.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("toggleVisibility")
fun View.toggleVisibility(allowShow: Boolean) {
    visibility = if (allowShow) View.VISIBLE else View.GONE
}