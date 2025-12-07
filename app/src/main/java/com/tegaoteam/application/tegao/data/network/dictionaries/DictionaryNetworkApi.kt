package com.tegaoteam.application.tegao.data.network.dictionaries

import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Dictionary

interface DictionaryNetworkApi {
    val dict: Dictionary?

    suspend fun searchWord(keyword: String): RepoResult<Any>
    suspend fun searchKanji(keyword: String): RepoResult<Any>
    suspend fun devTest(keyword: String): RepoResult<String>
}