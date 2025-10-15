package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

object MaziiResponseConverter {
    fun toDomainWordList(json: JsonObject): List<Word> {
        //TODO
        return listOf(Word(
            id = 0,
            reading = "Mazii WORD fetching success",
            furigana = "JSON size: ${json.size()}",
            definitions = mutableListOf()))
    }
    fun toDomainKanjiList(json: JsonObject): List<Kanji> {
        //TODO
        return listOf(Kanji(
            id = 0,
            character = "Mazii KANJI fetching success",
            meaning = "JSON size: ${json.size()}",
            strokeCount = 0,
            frequency = 0
        ))
    }
}