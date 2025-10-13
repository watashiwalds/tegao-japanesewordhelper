package com.tegaoteam.application.tegao.data.network.api

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.converter.MaziiJsonConverter
import com.tegaoteam.application.tegao.domain.interf.DictionaryApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import timber.log.Timber

object MaziiDictionaryApi: DictionaryApi {
    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == "mazii" }

    private lateinit var _url: String
    private lateinit var _wordPath: String
    private lateinit var _wordPayloadRequest: JsonObject
    private lateinit var _kanjiPath: String
    private lateinit var _kanjiPayloadRequest: JsonObject
    init {
        dict?.let {
            _url = it.jsonObject.get(Dictionary.ONL_URL).asString
            _wordPath = it.jsonObject.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordPayloadRequest = it.jsonObject.get(Dictionary.ONL_WORD_PAYLOADREQUEST).asJsonObject
            _kanjiPath = it.jsonObject.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPayloadRequest = it.jsonObject.get(Dictionary.ONL_KANJI_PAYLOADREQUEST).asJsonObject
        }
    }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(_url).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): List<Word> {
        _wordPayloadRequest.addProperty("query", keyword)
        val data = instance.fetchJsonObject(endpoint = _wordPath, body = _wordPayloadRequest)
        return MaziiJsonConverter.toDomainWordList(data)
    }

    override suspend fun searchKanji(keyword: String): List<Kanji> {
        _kanjiPayloadRequest.addProperty("query", keyword)
        val data = instance.fetchJsonObject(endpoint = _kanjiPath, body = _kanjiPayloadRequest)
        return MaziiJsonConverter.toDomainKanjiList(data)
    }

    override suspend fun indevTest(keyword: String): String {
        _wordPayloadRequest.addProperty("query", keyword)
        val data = instance.fetchJsonObject(endpoint = _wordPath, body = _wordPayloadRequest)
        return "$data"
    }
}