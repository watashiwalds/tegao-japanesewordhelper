package com.tegaoteam.application.tegao.ui.component.tag

import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.TermBank

data class TagItem(
    val label: String,
    val backgroundColor: Int,
    val textColor: Int,
    val detail: String? = null,
    val clickListener: (TagItem) -> Unit = {}
) {
    fun onClick() {
        clickListener.invoke(this)
    }

    companion object {
        fun toTagItem(termKey: String, label: String? = null, detail: String? = null): TagItem {
            val colorPair = TermBank.getTermColor(termKey)
            return TagItem(
                label = label.takeUnless { label.isNullOrBlank() }?: TermBank.getTermLabel(termKey),
                backgroundColor = colorPair.first,
                textColor = colorPair.second,
                detail = detail.takeUnless { detail.isNullOrBlank() }?: TermBank.getTermDescription(termKey),
                clickListener = { tagItem -> AppToast.show(tagItem.detail.toString(), AppToast.LENGTH_SHORT)}
            )
        }
    }
}