package com.tegaoteam.application.tegao.data.network.dictionaries.jisho

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import okhttp3.ResponseBody

class JishoResponseConverter: DictionaryResponseConverter {
    override fun <T> toDomainWordList(rawData: T): List<Word> {
        //TODO
        val words = mutableListOf<Word>()
        if (rawData !is JsonObject) return words
        words.add(Word(
            id = 0,
            reading = "",
            furigana = listOf("Jisho WORD fetching success, size: ${rawData.size()}"),
            definitions = mutableListOf()
        ))
        return words
    }
    override fun <T> toDomainKanjiList(rawData: T): List<Kanji> {
        //TODO
        val kanjis = mutableListOf<Kanji>()
        if (rawData !is ResponseBody) return kanjis
        kanjis.add(Kanji(
            id = 0,
            character = "",
            meaning = "Jisho KANJI fetching success, size: ${rawData.string().length}"
        ))
        return kanjis
    }
}