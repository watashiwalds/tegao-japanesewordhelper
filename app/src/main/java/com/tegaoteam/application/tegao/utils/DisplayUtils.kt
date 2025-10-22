package com.tegaoteam.application.tegao.utils

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("toggleVisibility")
fun View.toggleVisibility(allowShow: Boolean) {
    visibility = if (allowShow) View.VISIBLE else View.GONE
}

@BindingAdapter("toggleVisibilityLiveData")
fun View.toggleVisibilityLiveData(allowShow: LiveData<Boolean>) {
    visibility = if (allowShow.value?: false) View.VISIBLE else View.GONE
}

@BindingAdapter("toggleVisibilityLiveDataReverse")
fun View.toggleVisibilityLiveDataReverse(allowShow: LiveData<Boolean>) {
    visibility = if (allowShow.value?: false) View.GONE else View.VISIBLE
}

@BindingAdapter("toggleVisibility", "toggleVisibility2ndGateLiveData")
fun View.twoConditionToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>) {
    visibility = if (allowShow && shouldShow.value?: false) View.VISIBLE else View.GONE
}

@BindingAdapter("toggleVisibility", "toggleVisibility2ndGateLiveDataReverse")
fun View.twoConditionReverseToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>) {
    visibility = if (allowShow && !(shouldShow.value?: false)) View.VISIBLE else View.GONE
}

@BindingAdapter("goneWhenEmpty")
fun TextView.goneWhenEmpty(enable: Boolean) {
    visibility = if (enable && text.toString().isBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("goneByTextValue")
fun Group.goneByStringValue(s: String) {
    if (s.isBlank()) visibility = View.GONE else View.VISIBLE
}