package com.tegaoteam.application.tegao.ui.component.flickcard

import androidx.databinding.BindingAdapter

@BindingAdapter("flickable")
fun FlickableView.setFlickable(value: Boolean) {
    flickable = value
}