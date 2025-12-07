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

class JishoDictionaryApi private constructor():
    DictionaryNetworkApi {

    companion object {
        val api by lazy {
            JishoDictionaryApi()
        }
    }

    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == "jisho" }

    private var _url: String
    private var _wordPath: String
    private var _wordParams: JsonObject
    private var _kanjiPath: String
    private var _kanjiPathAppend: String
    init {
        Gson().fromJson(dict?.jsonInfos, JsonObject::class.java).let {
            _url = it.get(Dictionary.ONL_URL).asString
            _wordPath = it.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordParams = it.get(Dictionary.ONL_WORD_PARAMREQUEST).asJsonObject
            _kanjiPath = it.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPathAppend = it.get(Dictionary.ONL_KANJI_PATHAPPEND).asString
        }
    }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(_url).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): RepoResult<JsonObject> {
        _wordParams.addProperty("keyword", keyword)
        val res = RetrofitResult.wrapper { instance.getFunctionFetchJson(endpoint = _wordPath, params = _wordParams.toMap().mapValues { it.value.toString() }) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonObject)
        }
    }

    override suspend fun searchKanji(keyword: String): RepoResult<ResponseBody> {
        _kanjiPathAppend = String.format(_kanjiPathAppend, keyword)
        val res = RetrofitResult.wrapper { instance.getFunctionFetchRaw(endpoint = _kanjiPath + _kanjiPathAppend, params = mapOf()) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<ResponseBody> -> RepoResult.Success(res.data)
        }
    }

    override suspend fun devTest(keyword: String): RepoResult<String> {
        _wordParams.addProperty("keyword", keyword)
        val res = RetrofitResult.wrapper { instance.getFunctionFetchJson(endpoint = _wordPath, params = _wordParams.toMap().mapValues { it.value.toString() }) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<*> -> RepoResult.Success("${res.data}")
        }
    }
}