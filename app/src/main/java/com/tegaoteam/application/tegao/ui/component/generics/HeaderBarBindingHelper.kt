package com.tegaoteam.application.tegao.ui.component.generics

import androidx.databinding.ViewDataBinding
import com.tegaoteam.application.tegao.BR

/**
 * Binding helper for header bar (having <variable> of name "headerBarInfo" type HeaderBarInfo)
 *
 * In my imagination, only component_label_header_bar would exist (only usecase)
 *
 * Use bind() and required value to bind
 */
object HeaderBarBindingHelper {
    fun bind(
        headerBinding: ViewDataBinding,
        label: String,
        backOnClickListener: (() -> Unit)? = null) {
        val info = HeaderBarInfo(label, backOnClickListener)
        headerBinding.setVariable(BR.headerBarInfo, info)
        headerBinding.executePendingBindings()
    }
}