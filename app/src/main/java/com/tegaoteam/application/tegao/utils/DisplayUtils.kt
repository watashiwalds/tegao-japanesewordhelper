package com.tegaoteam.application.tegao.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("toggleVisibility")
fun View.toggleVisibility(allowShow: Boolean) {
    visibility = if (allowShow) View.VISIBLE else View.GONE
}

@BindingAdapter("toggleVisibility", "toggleVisibilityByLiveData")
fun View.twoConditionToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>) {
    visibility = if (allowShow && shouldShow.value?: false) View.VISIBLE else View.GONE
}

@BindingAdapter("toggleVisibility", "toggleVisibilityByLiveDataReverse")
fun View.twoConditionReverseToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>) {
    visibility = if (allowShow && !(shouldShow.value?: false)) View.VISIBLE else View.GONE
}

@BindingAdapter("goneWhenEmpty")
fun TextView.goneWhenEmpty(enable: Boolean) {
    if (enable && text.toString().isBlank()) visibility = View.GONE
}