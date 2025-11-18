package com.tegaoteam.application.tegao.ui.component.flickcard

import androidx.databinding.BindingAdapter

@BindingAdapter("flickable")
fun FlickableConstraintLayout.setFlickable(value: Boolean) {
    flickable = value
}