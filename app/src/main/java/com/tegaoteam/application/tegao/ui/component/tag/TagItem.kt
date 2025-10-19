package com.tegaoteam.application.tegao.ui.component.tag

data class TagItem(
    val id: Int,
    val label: String,
    val color: String,
    val detail: String? = null,
    val clickListener: () -> Unit = {}
) {
    fun onClick() {
        clickListener.invoke()
    }
}