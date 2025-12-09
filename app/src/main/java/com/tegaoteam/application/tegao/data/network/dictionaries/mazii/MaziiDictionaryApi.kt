package com.tegaoteam.application.tegao.data.network.dictionaries.mazii

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.config.SystemStates
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.network.ErrorResults

class MaziiDictionaryApi private constructor(): DictionaryNetworkApi {
    companion object {
        const val DICTIONARY_ID = "mazii"

        private val rootUrl = "https://mazii.net/api/search/"

        private val endpointWord = "word"
        private val payloadWord = mapOf(
            "dict" to "javi",
            "type" to "word",
            "query" to "",
            "limit" to "5",
            "page" to "1"
        )

        private val endpointKanji = "kanji"
        private val payloadKanji = mapOf(
            "dict" to "javi",
            "type" to "kanji",
            "query" to ""
        )

        val api by lazy {
            MaziiDictionaryApi()
        }
    }

    private val gson = Gson()
    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == DICTIONARY_ID }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(rootUrl, RetrofitMaker.TYPE_BROWSING).create(RetrofitApi::class.java)
    }

    private var _wordPayloadObj: JsonObject = gson.toJsonTree(payloadWord).asJsonObject
    private var _kanjiPayloadObj: JsonObject = gson.toJsonTree(payloadKanji).asJsonObject

    override suspend fun searchWord(keyword: String): RepoResult<JsonObject> {
        if (SystemStates.isInternetAvailable() != true) return ErrorResults.NO_INTERNET_CONNECTION

        _wordPayloadObj.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = endpointWord, params = mapOf(), body = _wordPayloadObj) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonObject)
        }
    }

    override suspend fun searchKanji(keyword: String): RepoResult<JsonObject> {
        if (SystemStates.isInternetAvailable() != true) return ErrorResults.NO_INTERNET_CONNECTION

        _kanjiPayloadObj.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = endpointKanji, params = mapOf(), body = _kanjiPayloadObj) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonObject)
        }
    }

    override suspend fun devTest(keyword: String): RepoResult<String> {
        _wordPayloadObj.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = endpointWord, params = mapOf(), body = _wordPayloadObj) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<*> -> RepoResult.Success("${res.data}")
        }
    }
}