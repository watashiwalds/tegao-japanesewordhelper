package com.tegaoteam.application.tegao.domain.interf

import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.model.RepoResult

interface DictionaryRepo {
    suspend fun searchWord(keyword: String, dictionaryId: String): RepoResult<List<Word>>
    suspend fun searchKanji(keyword: String, dictionaryId: String): RepoResult<List<Kanji>>
    suspend fun devTest(keyword: String, dictionaryId: String): RepoResult<String>
}