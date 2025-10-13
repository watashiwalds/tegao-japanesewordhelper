package com.tegaoteam.application.tegao.domain.type

data class Kanji(
    val id: Int,
    val character: String,
    var kunyomi: String? = null,
    var onyomi: String? = null,
    var composites: MutableList<Pair<String, String>>? = null,
    val meaning: String,
    var details: String? = null,
    val jlpt: Int,
    val strokeCount: Int,
    val grade: Int,
    val frequency: Int,
    var additionalInfo: String? = null
)