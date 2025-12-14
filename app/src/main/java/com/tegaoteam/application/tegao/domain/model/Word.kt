package com.tegaoteam.application.tegao.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val id: String,
    val reading: String,
    val furigana: List<String>? = null,
    var tags: List<Tag>? = null,
    var additionalInfo: List<AdditionalInfo>? = null,
    val definitions: List<Definition>
) {

    @Serializable
    data class Definition(
        var tags: List<Tag>? = null,
        val meaning: String,
        var expandInfos: List<ExpandInfo>? = null
    ) {

        @Serializable
        data class Tag(
            val termKey: String,
            val label: String,
            val description: String
        )

        @Serializable
        data class ExpandInfo(
            val termKey: String,
            val content: String
        )
    }

    @Serializable
    data class Tag(
        val termKey: String,
        val label: String,
        val description: String? = null
    )

    @Serializable
    data class AdditionalInfo(
        val termKey: String,
        val content: String
    )

    companion object {
        const val TYPE_VALUE = 0

        fun default() = Word(
            id = "0",
            reading = "",
            definitions = listOf()
        )
    }
}
