package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.passing.RepoResult

interface DictionaryRepo {
    val dict: Dictionary?

    suspend fun searchWord(keyword: String): RepoResult<List<Word>>
    suspend fun searchKanji(keyword: String): RepoResult<List<Kanji>>

    suspend fun devTest(keyword: String): RepoResult<String>
}