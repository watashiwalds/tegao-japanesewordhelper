package com.tegaoteam.application.tegao.ui.component.tag

data class TagItem(
    val label: String,
    val color: Int,
    val detail: String? = null,
    val clickListener: (TagItem) -> Unit = {}
) {
    fun onClick() {
        clickListener.invoke(this)
    }
}