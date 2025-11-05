package com.tegaoteam.application.tegao.data.network.dictionaries

import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

interface DictionaryResponseConverter {
    fun <T> toDomainWordList(rawData: T): List<Word>
    fun <T> toDomainKanjiList(rawData: T): List<Kanji>
}