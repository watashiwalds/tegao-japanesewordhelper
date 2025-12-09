package com.tegaoteam.application.tegao.data.network.dictionaries.jisho

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.utils.toMap
import okhttp3.ResponseBody

class JishoDictionaryApi private constructor(): DictionaryNetworkApi {
    companion object {
        const val DICTIONARY_ID = "jisho"

        private val rootUrl = "https://jisho.org/"

        private val endpointWord = "api/v1/search/words"
        private val paramsWord = mapOf(
            "keyword" to ""
        )

        private val endpointKanji = "search/"
        private val endpointKanjiAppend = "%s#kanji"

        val api by lazy {
            JishoDictionaryApi()
        }
    }

    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == DICTIONARY_ID }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(rootUrl, RetrofitMaker.TYPE_BROWSING).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): RepoResult<JsonObject> {
        val wordFormedParams = paramsWord.toMutableMap().apply { set("keyword", keyword) }
        val res = RetrofitResult.wrapper { instance.getFunctionFetchJson(endpoint = endpointWord, params = wordFormedParams) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonObject)
        }
    }

    override suspend fun searchKanji(keyword: String): RepoResult<ResponseBody> {
        val kanjiFormedAppend = String.format(endpointKanjiAppend, keyword)
        val res = RetrofitResult.wrapper { instance.getFunctionFetchRaw(endpoint = endpointKanji + kanjiFormedAppend, params = mapOf()) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<ResponseBody> -> RepoResult.Success(res.data)
        }
    }

    override suspend fun devTest(keyword: String): RepoResult<String> {
        val wordFormedParams = paramsWord.toMutableMap().apply { set("keyword", keyword) }
        val res = RetrofitResult.wrapper { instance.getFunctionFetchJson(endpoint = endpointWord, params = wordFormedParams) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<*> -> RepoResult.Success("${res.data}")
        }
    }
}