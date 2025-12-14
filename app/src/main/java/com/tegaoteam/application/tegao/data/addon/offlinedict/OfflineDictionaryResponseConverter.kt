package com.tegaoteam.application.tegao.data.addon.offlinedict

import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

class OfflineDictionaryResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        return listOf(
            Word(
                id = "0",
                reading = "",
                furigana = listOf(rawData.toString()),
                definitions = listOf()
            )
        )
    }

    override fun <T> toDomainKanjiList(rawData: T): List<Kanji> {
        return listOf(
            Kanji(
                id = "0",
                character = "",
                meaning = rawData.toString()
            )
        )
    }
}