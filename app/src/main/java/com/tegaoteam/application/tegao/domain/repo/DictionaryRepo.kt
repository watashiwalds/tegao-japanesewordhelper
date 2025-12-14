package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.independency.Stream

interface DictionaryRepo {
    fun getAvailableDictionariesList(): List<Dictionary>

    suspend fun searchWord(keyword: String, dictionaryId: String): Stream<RepoResult<List<Word>?>>
    suspend fun searchKanji(keyword: String, dictionaryId: String): Stream<RepoResult<List<Kanji>?>>
}