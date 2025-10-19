package com.tegaoteam.application.tegao.ui.component.infotag

data class InfoTagItem(
    val label: String,
    val color: String,
    val detail: String? = null,
    val clickListener: () -> Unit = {}
) {
    fun onClick() {
        clickListener.invoke()
    }
}