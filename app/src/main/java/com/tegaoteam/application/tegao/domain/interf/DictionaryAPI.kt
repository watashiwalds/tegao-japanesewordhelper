package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.type.Kanji
import com.tegaoteam.application.tegao.domain.type.Word

interface DictionaryAPI {
    fun searchWord(keyword: String): List<Word>
    fun searchKanji(keyword: String): List<Kanji>
}