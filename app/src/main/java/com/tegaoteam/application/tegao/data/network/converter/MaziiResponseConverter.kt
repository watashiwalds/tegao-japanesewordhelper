package com.tegaoteam.application.tegao.data.network.converter

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

object MaziiResponseConverter {
    fun toDomainWordList(json: JsonObject): List<Word> {
        //TODO
        return listOf()
    }
    fun toDomainKanjiList(json: JsonObject): List<Kanji> {
        //TODO
        return listOf()
    }
}