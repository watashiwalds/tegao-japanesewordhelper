package com.tegaoteam.application.tegao.domain.repo

import com.tegaoteam.application.tegao.domain.interf.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.domain.independency.RepoResult

interface DictionaryRepo {
    fun getAvailableDictionariesList(): List<Dictionary>
    fun getAvailableDictionaryNetworkApis(): List<DictionaryNetworkApi>

    suspend fun searchWord(keyword: String, dictionaryId: String): RepoResult<List<Word>>
    suspend fun searchKanji(keyword: String, dictionaryId: String): RepoResult<List<Kanji>>
    suspend fun devTest(keyword: String, dictionaryId: String): RepoResult<String>
}