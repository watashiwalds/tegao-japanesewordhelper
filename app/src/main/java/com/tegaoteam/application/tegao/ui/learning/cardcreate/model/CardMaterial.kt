package com.tegaoteam.application.tegao.ui.learning.cardcreate.model

import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

data class CardMaterial(
    val contents: Map<String, List<String>>
) {
    companion object {
        fun fromWord(word: Word): CardMaterial {
            val parsed = mutableMapOf<String, List<String>>()
            if (word.reading.isNotBlank()) parsed["reading"] = listOf(word.reading)
            word.furigana?.let { parsed["furigana"] = it }
            parsed["meaning"] = word.definitions.map {
                (it.tags?.joinToString(separator = "") { tag -> "[${tag.label}] " }?: "") +
                it.meaning
            }.toList()
            //todo: Add definition's expandInfo as additional
            val defExpands = word.definitions.mapNotNull { it.expandInfos }.flatten().map { it.content }.toSet()
            if (defExpands.isNotEmpty()) parsed["expand"] = defExpands.toList()
            word.additionalInfo?.let { parsed["additional"] = it.map { additionalInfo -> additionalInfo.content } }
            return CardMaterial(parsed)
        }
        fun fromKanji(kanji: Kanji): CardMaterial {
            val parsed = mutableMapOf<String, List<String>>()
            if (kanji.character.isNotBlank()) parsed["reading"] = listOf(kanji.character)
            kanji.kunyomi?.let { parsed["kunyomi"] = it }
            kanji.onyomi?.let { parsed["onyomi"] = it }
            if (kanji.meaning.isNotBlank()) parsed["meaning"] = listOf(kanji.meaning)
            kanji.additionalInfo?.let { parsed["additional"] = it.map { additionalInfo -> additionalInfo.content } }
            return CardMaterial(parsed)
        }

        //todo: change to string res for globalization
        val keyDisplayMap = mapOf(
            "reading" to "Từ vựng",
            "meaning" to "Dịch nghĩa",
            "expand" to "Mở rộng",
            "furigana" to "Phiên âm Hiragana",
            "kunyomi" to "Nhật âm",
            "onyomi" to "Hán âm",
            "additional" to "Khác",
        )
    }
}