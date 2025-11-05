package com.tegaoteam.application.tegao.ui.component.tag

import androidx.core.content.ContextCompat
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.TermBank

data class TagItem(
    val label: String,
    val color: Int,
    val detail: String? = null,
    val clickListener: (TagItem) -> Unit = {}
) {
    fun onClick() {
        clickListener.invoke(this)
    }

    companion object {
        fun toTagItem(termKey: String, label: String? = null, detail: String? = null): TagItem {
            return TagItem(
                label = label.takeUnless { label.isNullOrBlank() }?: TermBank.getTermLabel(termKey),
                color = ContextCompat.getColor(TegaoApplication.instance.applicationContext, R.color.neutral),
                detail = detail.takeUnless { detail.isNullOrBlank() }?: TermBank.getTermDescription(termKey),
                clickListener = { tagItem -> AppToast.show(tagItem.detail.toString(), AppToast.LENGTH_SHORT)}
            )
        }
    }
}