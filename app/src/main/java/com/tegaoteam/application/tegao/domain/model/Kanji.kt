package com.tegaoteam.application.tegao.domain.model

data class Kanji(
    val id: Int,
    val character: String,
    var kunyomi: String? = null,
    var onyomi: String? = null,
    var composites: MutableList<Pair<String, String?>>? = null,
    val meaning: String,
    var details: String? = null,
    var tags: MutableList<Pair<String, String?>>? = null,
    var additionalInfo: MutableList<Pair<String, String>>? = null,
)