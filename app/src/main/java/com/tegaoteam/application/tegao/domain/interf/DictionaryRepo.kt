package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

interface DictionaryRepo {
    val dict: Dictionary?

    suspend fun searchWord(keyword: String): List<Word>
    suspend fun searchKanji(keyword: String): List<Kanji>

    suspend fun devTest(keyword: String): String
}