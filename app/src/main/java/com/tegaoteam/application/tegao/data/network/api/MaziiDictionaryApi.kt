package com.tegaoteam.application.tegao.data.network.api

import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.converter.MaziiJsonConverter
import com.tegaoteam.application.tegao.domain.interf.DictionaryApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word

object MaziiDictionaryApi: DictionaryApi {
    var dict: Dictionary?
        private set
    private lateinit var _url: String
    private lateinit var _wordPath: String
    private lateinit var _wordPayloadRequest: String
    private lateinit var _kanjiPath: String
    private lateinit var _kanjiPayloadRequest: String
    init {
        dict = DictionaryConfig.getDictionariesList().find { it.id == "mazii" }
        dict?.let {
            _url = it.jsonObject.get(Dictionary.ONL_URL).asString
            _wordPath = it.jsonObject.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordPayloadRequest = it.jsonObject.get(Dictionary.ONL_WORD_PAYLOADREQUEST).asString
            _kanjiPath = it.jsonObject.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPayloadRequest = it.jsonObject.get(Dictionary.ONL_KANJI_PAYLOADREQUEST).asString
        }
    }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(_url).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): List<Word> {
        val data = instance.fetchJsonObject(endpoint = _wordPath, body = _wordPayloadRequest.format(keyword))
        return MaziiJsonConverter.toDomainWordList(data)
    }

    override suspend fun searchKanji(keyword: String): List<Kanji> {
        val data = instance.fetchJsonObject(endpoint = _kanjiPath, body = _kanjiPayloadRequest.format(keyword))
        return MaziiJsonConverter.toDomainKanjiList(data)
    }

    override suspend fun indevTest(keyword: String): String {
        val data = instance.fetchJsonObject(endpoint = _wordPath, body = _wordPayloadRequest.format(keyword))
        return data.asString
    }
}