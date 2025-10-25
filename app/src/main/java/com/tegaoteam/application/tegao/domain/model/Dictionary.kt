package com.tegaoteam.application.tegao.domain.model

class Dictionary(
    val id: String,
    val displayName: String,
    val type: Int,
    val jsonInfos: String
) {
    companion object {
        const val ONL_URL = "url"

        const val ONL_WORD_URLPATH = "path_word"
        const val ONL_WORD_TYPE = "type_word"
        const val ONL_WORD_PAYLOADREQUEST = "payloadRequest_word"
        const val ONL_WORD_PARAMREQUEST = "paramRequest_word"
        const val ONL_WORD_PATHAPPEND = "pathAppend_word"

        const val ONL_KANJI_URLPATH = "path_kanji"
        const val ONL_KANJI_TYPE = "type_kanji"
        const val ONL_KANJI_PAYLOADREQUEST = "payloadRequest_kanji"
        const val ONL_KANJI_PARAMREQUEST = "paramRequest_kanji"
        const val ONL_KANJI_PATHAPPEND = "pathAppend_kanji"
    }
}