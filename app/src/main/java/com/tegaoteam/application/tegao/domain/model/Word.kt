package com.tegaoteam.application.tegao.domain.model

data class Word(
    val id: Int,
    val reading: String,
    val furigana: String,
    var tags: MutableList<Pair<String, String?>>? = null,
    var additionalInfo: MutableList<Pair<String, String>>? = null,
    val definitions: MutableList<Definition>
) {
    data class Definition (
        var tags: MutableList<Pair<String, String?>>? = null,
        val meaning: String,
        var expandInfos: MutableList<Pair<String, String>>? = null
    )
}
