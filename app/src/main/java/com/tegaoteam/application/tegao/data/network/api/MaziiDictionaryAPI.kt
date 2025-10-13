package com.tegaoteam.application.tegao.data.network.api

import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.network.DictionaryRetrofitClient
import com.tegaoteam.application.tegao.domain.interf.DictionaryAPI
import com.tegaoteam.application.tegao.domain.type.Dictionary
import com.tegaoteam.application.tegao.domain.type.Kanji
import com.tegaoteam.application.tegao.domain.type.Word
import retrofit2.http.POST
import retrofit2.http.Path

class MaziiDictionaryAPI(private val retrofitClient: DictionaryRetrofitClient): DictionaryAPI {
    companion object {
        val instance by lazy {
            MaziiDictionaryAPI(DictionaryRetrofitClient(DictionaryConfig.getDictionariesList().find { it.id == "mazii" }))
        }
    }

    private var _wordUrlPath: String? = null
    private var _kanjiUrlPath: String? = null
    private var _wordPayloadRequest: String? = null
    private var _kanjiPayloadRequest: String? = null

    init {
        retrofitClient.dict?.let {
            _wordUrlPath = it.jsonObject.get(Dictionary.ONL_WORD_URLPATH).asString
            _wordPayloadRequest = it.jsonObject.get(Dictionary.ONL_WORD_PAYLOADREQUEST).asString
            _kanjiUrlPath = it.jsonObject.get(Dictionary.ONL_KANJI_URLPATH).asString
            _kanjiPayloadRequest = it.jsonObject.get(Dictionary.ONL_KANJI_PAYLOADREQUEST).asString
        }
    }

    override fun searchWord(keyword: String): List<Word> {
        TODO("Not yet implemented")
    }

    override fun searchKanji(keyword: String): List<Kanji> {
        TODO("Not yet implemented")
    }
}