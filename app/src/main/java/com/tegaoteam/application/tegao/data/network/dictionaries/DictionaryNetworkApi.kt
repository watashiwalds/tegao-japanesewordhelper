package com.tegaoteam.application.tegao.data.network.dictionaries

import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

interface DictionaryNetworkApi {
    val dict: Dictionary?

    suspend fun searchWord(keyword: String): RepoResult<List<Word>>
    suspend fun searchKanji(keyword: String): RepoResult<List<Kanji>>
    suspend fun devTest(keyword: String): RepoResult<String>
}