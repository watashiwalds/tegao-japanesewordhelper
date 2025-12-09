package com.tegaoteam.application.tegao.domain.model

class Dictionary(
    val id: String,
    val displayName: String,
    val isOnline: Boolean,
    val supportType: Int
) {
    companion object {
        const val SUPPORT_ALL = 1
        const val SUPPORT_WORD = 2
        const val SUPPORT_KANJI = 3
    }
}