package com.tegaoteam.application.tegao.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("toggleVisibility")
fun View.toggleVisibility(allowShow: Boolean) {
    visibility = if (allowShow) View.VISIBLE else View.GONE
}
@BindingAdapter("toggleVisibilityLiveData")
        /**
         * If LiveData.value true then VISIBLE, else GONE
         */
fun View.toggleVisibilityLiveData(allowShow: LiveData<Boolean>?) {
    visibility = if (allowShow?.value?: false) View.VISIBLE else View.GONE
}
@BindingAdapter("toggleVisibilityLiveDataReverse")
        /**
         * If LiveData.value false then VISIBLE, else GONE
         */
fun View.toggleVisibilityLiveDataReverse(allowShow: LiveData<Boolean>?) {
    visibility = if (allowShow?.value?: false) View.GONE else View.VISIBLE
}
@BindingAdapter("toggleVisibility", "toggleVisibility2ndGateLiveData")
fun View.twoConditionToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>?) {
    shouldShow?.let {
        visibility = if (allowShow && shouldShow.value?: false) View.VISIBLE else View.GONE
        return
    }
    visibility = if (allowShow) View.VISIBLE else View.GONE
}
@BindingAdapter("toggleVisibility", "toggleVisibility2ndGateLiveDataReverse")
fun View.twoConditionReverseToggleVisibility(allowShow: Boolean, shouldShow: LiveData<Boolean>) {
    visibility = if (allowShow && !(shouldShow.value?: false)) View.VISIBLE else View.GONE
}
@BindingAdapter("setTextWithVisibility")
fun TextView.setTextWithVisibility(textValue: String?) {
    visibility = if (textValue.isNullOrBlank()) {
        View.GONE
    } else {
        text = textValue
        View.VISIBLE
    }
}

@BindingAdapter("srcResId")
fun ImageView.setSrcWithResId(drawableResId: Int) {
    if (drawableResId != 0) setImageResource(drawableResId) else visibility = View.GONE
}
@BindingAdapter("textResId")
fun TextView.setTextWithResId(stringResId: Int) {
    if (stringResId != 0) setText(stringResId) else visibility = View.GONE
}