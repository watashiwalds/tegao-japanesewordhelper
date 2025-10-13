package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.type.Kanji
import com.tegaoteam.application.tegao.domain.type.Word

interface DictionaryAPI {
    suspend fun searchWord(keyword: String): List<Word>
    suspend fun searchKanji(keyword: String): List<Kanji>

    suspend fun indevTest(keyword: String): String
}