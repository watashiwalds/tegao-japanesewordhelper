package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.RepoResult
import com.tegaoteam.application.tegao.domain.model.Word

object DictionaryHub {
    private val availApis = DictionaryConfig.getDictionariesApi()
    private fun getDictionaryApiById(dictId: String) = availApis.firstOrNull() { it.dict?.id == dictId }

    suspend fun searchWord(keyword: String): RepoResult<List<Word>> {
//        TODO("Not yet implemented")
        return RepoResult.Success(listOf())
    }

    suspend fun searchKanji(keyword: String): RepoResult<List<Kanji>> {
//        TODO("Not yet implemented")
        return RepoResult.Success(listOf())
    }

    suspend fun devTest(keyword: String, dictId: String): RepoResult<String> {
        val requestedApi = getDictionaryApiById(dictId)
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