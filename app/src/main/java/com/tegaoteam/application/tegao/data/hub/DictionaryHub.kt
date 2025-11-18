package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.repo.DictionaryRepo
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Word

class DictionaryHub: DictionaryRepo {
    private fun getAvailableDictionaryPacks(): Map<DictionaryNetworkApi, DictionaryResponseConverter> = DictionaryConfig.getDictionariesPack()
    private fun getDictionaryApiById(dictId: String) = getAvailableDictionaryPacks().keys.firstOrNull { it.dict?.id == dictId }
    private fun getCompatDictionaryResponseConverter(dictId: String) = getAvailableDictionaryPacks().getOrDefault(getDictionaryApiById(dictId), null)

    override fun getAvailableDictionariesList(): List<Dictionary> = DictionaryConfig.getDictionariesList()

    override suspend fun searchWord(keyword: String, dictionaryId: String): RepoResult<List<Word>?> {
        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchWord(keyword)
            return when (res) {
                is RepoResult.Error<*> -> res
                is RepoResult.Success<*> -> RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainWordList(res.data))
            }
        }
        return RepoResult.Error<String>(message = "dictId not found (unsupported or misvalued)")
    }

    override suspend fun searchKanji(keyword: String, dictionaryId: String): RepoResult<List<Kanji>?> {
        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchKanji(keyword)
            return when (res) {
                is RepoResult.Error<*> -> res
                is RepoResult.Success<*> -> RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainKanjiList(res.data))
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