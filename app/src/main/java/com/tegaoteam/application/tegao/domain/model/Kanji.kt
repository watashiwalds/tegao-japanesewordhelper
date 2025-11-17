package com.tegaoteam.application.tegao.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Kanji(
    val id: Int,
    val character: String,
    var kunyomi: List<String>? = null,
    var onyomi: List<String>? = null,
    var composites: List<Composite>? = null,
    val meaning: String,
    var tags: List<Tag>? = null,
    var additionalInfo: List<AdditionalInfo>? = null,
) {

    @Serializable
    data class Composite(
        val character: String,
        val hanji: String?
    )

    @Serializable
    data class Tag(
        val termKey: String,
        val label: String
    )

    @Serializable
    data class AdditionalInfo(
        val termKey: String,
        val content: String
    )

    companion object {
        const val TYPE_VALUE = 1

        fun default() = Kanji(
            id = 0,
            character = "",
            meaning = ""
        )
    }
}