package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import okhttp3.ResponseBody

object JishoResponseConverter {
    fun toDomainWordList(json: JsonObject): List<Word> {
        //TODO
        return listOf(Word(
            id = 0,
            reading = "Jisho WORD fetching success",
            furigana = "JSON size: ${json.size()}",
            definitions = mutableListOf()))
    }
    fun toDomainKanjiList(raw: ResponseBody): List<Kanji> {
        //TODO
        return listOf(Kanji(
            id = 0,
            character = "Jisho KANJI fetching success",
            meaning = "RAW size: ${raw.string().length}",
            strokeCount = 0,
            frequency = 0
        ))
    }
}