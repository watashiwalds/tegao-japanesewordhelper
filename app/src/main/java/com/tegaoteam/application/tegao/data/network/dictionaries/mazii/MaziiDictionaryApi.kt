package com.tegaoteam.application.tegao.data.network.dictionaries.mazii

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.data.network.dictionaries.DictionaryNetworkApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.independency.RepoResult

class MaziiDictionaryApi private constructor(): DictionaryNetworkApi {

    companion object {
        val api by lazy {
            MaziiDictionaryApi()
        }
    }

    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == "mazii" }

    private var _url: String
    private var _wordPath: String
    private var _wordPayloadRequest: JsonObject
    private var _kanjiPath: String
    private var _kanjiPayloadRequest: JsonObject
    init {
        Gson().fromJson(dict?.jsonInfos, JsonObject::class.java).let {
            _url = it.get(Dictionary.ONL_URL).asString
            _wordPath = it.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordPayloadRequest = it.get(Dictionary.ONL_WORD_PAYLOADREQUEST).asJsonObject
            _kanjiPath = it.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPayloadRequest = it.get(Dictionary.ONL_KANJI_PAYLOADREQUEST).asJsonObject
        }
    }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(_url).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): RepoResult<JsonObject> {
        _wordPayloadRequest.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = _wordPath, params = mapOf(), body = _wordPayloadRequest) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonObject> -> RepoResult.Success(res.data)
        }
    }

    override suspend fun searchKanji(keyword: String): RepoResult<JsonObject> {
        _kanjiPayloadRequest.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = _kanjiPath, params = mapOf(), body = _kanjiPayloadRequest) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonObject> -> RepoResult.Success(res.data)
        }
    }

    override suspend fun devTest(keyword: String): RepoResult<String> {
        _wordPayloadRequest.addProperty("query", keyword)
        val res = RetrofitResult.wrapper { instance.postFunctionFetchJson(endpoint = _wordPath, params = mapOf(), body = _wordPayloadRequest) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<*> -> RepoResult.Success("${res.data}")
        }
    }
}