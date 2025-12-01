package com.tegaoteam.application.tegao.ui.component.learningcard

import androidx.databinding.BindingAdapter

@BindingAdapter("flickable")
fun FlickableConstraintLayout.setFlickable(value: Boolean) {
    flickable = value
}