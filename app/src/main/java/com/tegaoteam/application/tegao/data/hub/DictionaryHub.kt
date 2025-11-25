package com.tegaoteam.application.tegao.data.hub

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.database.SQLiteDatabase
import com.tegaoteam.application.tegao.data.database.dictionarycache.DictionaryCacheEntity
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryResponseConverter
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.repo.DictionaryRepo
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.utils.Time
import com.tegaoteam.application.tegao.utils.getMD5HashedValue
import timber.log.Timber

class DictionaryHub: DictionaryRepo {
    private fun getAvailableDictionaryPacks(): Map<DictionaryNetworkApi, DictionaryResponseConverter> = DictionaryConfig.getDictionariesPack()
    private fun getDictionaryApiById(dictId: String) = getAvailableDictionaryPacks().keys.firstOrNull { it.dict?.id == dictId }
    private fun getCompatDictionaryResponseConverter(dictId: String) = getAvailableDictionaryPacks().getOrDefault(getDictionaryApiById(dictId), null)

    override fun getAvailableDictionariesList(): List<Dictionary> = DictionaryConfig.getDictionariesList()

    override suspend fun searchWord(keyword: String, dictionaryId: String): RepoResult<List<Word>?> {
        val cache = getDictionaryCache(keyword, Word.TYPE_VALUE, dictionaryId)
        cache?.let{
            // cache older than 7 days -> force refresh, newer -> return as result
            if (Time.preciseTimeDifferenceBetween(it.cacheDate, Time.getCurrentTimestamp(), Time.DIFF_DAY) < 7) {
                return RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainWordList(cache.json))
            }
        }

        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchWord(keyword)
            return when (res) {
                is RepoResult.Error<*> -> {
                    // failed to search but have cache -> show cache
                    if (cache != null) RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainWordList(cache.json))
                    else res
                }
                is RepoResult.Success<*> -> {
                    // attempt to upsert new response from net to cache
                    upsertDictionaryCache(keyword, Word.TYPE_VALUE, dictionaryId, res.data, cache)
                    RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainWordList(res.data))
                }
            }
        }
        return RepoResult.Error<String>(message = "dictId not found (unsupported or misvalued)")
    }

    override suspend fun searchKanji(keyword: String, dictionaryId: String): RepoResult<List<Kanji>?> {
        val cache = getDictionaryCache(keyword, Kanji.TYPE_VALUE, dictionaryId)
        cache?.let{
            // cache older than 7 days -> force refresh, newer -> return as result
            if (Time.preciseTimeDifferenceBetween(it.cacheDate, Time.getCurrentTimestamp(), Time.DIFF_DAY) < 7) {
                return RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainKanjiList(cache.json))
            }
        }

        val requestedApi = getDictionaryApiById(dictionaryId)
        requestedApi?.let {
            val res = requestedApi.searchKanji(keyword)
            return when (res) {
                is RepoResult.Error<*> -> {
                    // failed to search but have cache -> show cache
                    if (cache != null) RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainKanjiList(cache.json))
                    else res
                }
                is RepoResult.Success<*> -> {
                    // attempt to upsert new response from net to cache
                    upsertDictionaryCache(keyword, Kanji.TYPE_VALUE, dictionaryId, res.data, cache)
                    RepoResult.Success(getCompatDictionaryResponseConverter(dictionaryId)?.toDomainKanjiList(res.data))
                }
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

    // cache get/check
    private val _cacheDAO = SQLiteDatabase.getInstance().dictionaryCacheDAO
    private fun getDictionaryCache(keyword: String, type: Int, dictId: String): DictionaryCacheEntity? {
        val res = _cacheDAO.getCache(keyword, type, dictId)
        Timber.i("Fetched cache for [$keyword $type $dictId]: ${res?.jsonMD5}")
        return res
    }
    private suspend fun upsertDictionaryCache(keyword: String, type: Int, dictId: String, newCacheData: Any?, oldCache: DictionaryCacheEntity? = null): Long {
        if (newCacheData == null) return 0
        val newCacheMD5 = getMD5HashedValue(newCacheData)
        oldCache?.let { if (newCacheMD5 == it.jsonMD5) return 0 }

        val newCacheEntity = DictionaryCacheEntity(
            keyword = keyword,
            type = type,
            dictionary = dictId,
            cacheDate = Time.getCurrentTimestamp().toString(),
            jsonMD5 = newCacheMD5,
            json = newCacheData.toString()
        )

        val resCode = _cacheDAO.upsertCache(newCacheEntity)
        Timber.i("Attempt upsert cache: ${newCacheEntity.jsonMD5} with result $resCode")
        return resCode
    }
}