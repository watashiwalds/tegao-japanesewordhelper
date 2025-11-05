package com.tegaoteam.application.tegao.domain.model

data class Word(
    val id: Int,
    val reading: String,
    val furigana: List<String>? = null,
    // app-handle tags, (termKey, display label)
    var tags: List<Pair<String, String>>? = null,
    var additionalInfo: List<Pair<String, String>>? = null,
    val definitions: List<Definition>
) {
    data class Definition (
        // converter-handle tags, (termKey, (label, description))
        var tags: List<Pair<String, Pair<String, String>>>? = null,
        val meaning: String,
        // (additional tag key, additional info by string)
        var expandInfos: List<Pair<String, String>>? = null
    )

    companion object {
        fun default() = Word(
            id = 0,
            reading = "",
            definitions = listOf()
        )
    }
}
