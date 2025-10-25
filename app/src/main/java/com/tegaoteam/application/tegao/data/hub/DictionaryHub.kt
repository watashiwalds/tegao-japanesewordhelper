package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.domain.interf.DictionaryRepo
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.RepoResult
import com.tegaoteam.application.tegao.domain.model.Word

class DictionaryHub: DictionaryRepo {
    private val availApis = DictionaryConfig.getDictionariesApi()
    private fun getDictionaryApiById(dictId: String) = availApis.firstOrNull() { it.dict?.id == dictId }

    override suspend fun searchWord(keyword: String, dictionaryId: String): RepoResult<List<Word>> {
        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchWord(keyword)
            return when (res) {
                is RepoResult.Error<*> -> res
                is RepoResult.Success<List<Word>> -> RepoResult.Success(res.data)
            }
        }
        return RepoResult.Error<String>(message = "dictId not found (unsupported or misvalued)")
    }

    override suspend fun searchKanji(keyword: String, dictionaryId: String): RepoResult<List<Kanji>> {
        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchKanji(keyword)
            return when (res) {
                is RepoResult.Error<*> -> res
                is RepoResult.Success<List<Kanji>> -> RepoResult.Success(res.data)
            }
        }
        return RepoResult.Error<String>(message = "dictId not found (unsupported or misvalued)")
    }

    override suspend fun devTest(keyword: String, dictionaryId: String): RepoResult<String> {
        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.devTest(keyword)
            return when (res) {
                is RepoResult.Error<*> -> res
                is RepoResult.Success<*> -> RepoResult.Success("${res.data}")
            }
        }
        return RepoResult.Error<String>(message = "dictId not found (unsupported or misvalued)")
    }
}