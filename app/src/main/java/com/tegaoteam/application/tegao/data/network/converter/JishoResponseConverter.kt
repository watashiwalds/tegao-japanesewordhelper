package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
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
            reading = "Jisho WORD fetching success",
            furigana = "JSON size: ${rawData.size()}",
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
            character = "Jisho KANJI fetching success",
            meaning = "RAW size: ${rawData.string().length}",
            strokeCount = 0,
            frequency = 0
        ))
        return kanjis
    }
}