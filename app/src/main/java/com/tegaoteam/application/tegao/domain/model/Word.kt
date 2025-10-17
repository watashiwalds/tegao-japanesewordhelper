package com.tegaoteam.application.tegao.domain.model

data class Word(
    val id: Int,
    val reading: String,
    val furigana: String,
    var tags: MutableList<String>? = null,
    var additionalInfo: String? = null,
    val definitions: MutableList<Definition>
) {
    data class Definition (
        var tags: MutableList<String>? = null,
        val meaning: String,
        var expandInfos: MutableList<Pair<String, String>>? = null
    )
}
