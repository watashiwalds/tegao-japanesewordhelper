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

object JishoDictionaryApi: DictionaryApi {
    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == "jisho" }

    private lateinit var _url: String
    private lateinit var _wordPath: String
    private lateinit var _wordParams: JsonObject
    private lateinit var _kanjiPath: String
    private lateinit var _kanjiPathAppend: String
    init {
        dict?.let {
            _url = it.jsonObject.get(Dictionary.ONL_URL).asString
            _wordPath = it.jsonObject.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordParams = it.jsonObject.get(Dictionary.ONL_WORD_PARAMREQUEST).asJsonObject
            _kanjiPath = it.jsonObject.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPathAppend = it.jsonObject.get(Dictionary.ONL_KANJI_PATHAPPEND).asString
        }
    }
    private val instance: RetrofitApi by lazy {
        RetrofitMaker.createWithUrl(_url).create(RetrofitApi::class.java)
    }

    override suspend fun searchWord(keyword: String): List<Word> {
        _wordParams.addProperty("keyword", keyword)
        val data = instance.getFunctionFetchJson(endpoint = _wordPath, params = mapOf())
        //TODO: Change to Jisho Converter
        return MaziiJsonConverter.toDomainWordList(data)
    }

    override suspend fun searchKanji(keyword: String): List<Kanji> {
        _kanjiPathAppend = String.format(_kanjiPathAppend, keyword)
        val data = instance.postFunctionFetchJson(endpoint = _kanjiPath + _kanjiPathAppend, params = mapOf(), body = JsonObject())
        //TODO: Change to Jisho Converter
        return MaziiJsonConverter.toDomainKanjiList(data)
    }

    override suspend fun indevTest(keyword: String): String {
        _wordParams.addProperty("keyword", keyword)
        val data = instance.getFunctionFetchJson(endpoint = _wordPath, params = mapOf())
        return "$data"
    }
}