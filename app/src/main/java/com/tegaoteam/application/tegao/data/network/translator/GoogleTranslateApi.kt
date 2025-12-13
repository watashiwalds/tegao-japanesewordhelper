package com.tegaoteam.application.tegao.data.network.translator

import com.tegaoteam.application.tegao.data.config.TranslatorConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.config.SystemStates
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.domain.model.Translator.Companion.Language as Language

class GoogleTranslateApi private constructor(): TranslatorApi{
    companion object {
        val api by lazy { GoogleTranslateApi() }

        private val rootUrl = "https://translate.googleapis.com/translate_a/"
        private val endpoint = "single"
        private val params = mapOf(
            "client" to "gtx",
            "sl" to "",
            "tl" to "",
            "dt" to "t",
            "q" to ""
        )

        private val langIds = mapOf<Language, String>(
            Language.JAPANESE to "ja",
            Language.VIETNAMESE to "vi",
            Language.ENGLISH to "en"
        )
    }

    override val translator = TranslatorConfig.TRANSLATOR_GOOGLETRANSLATE
    private val retrofit by lazy { RetrofitMaker.createWithUrl(rootUrl, RetrofitMaker.TYPE_BROWSING).create(RetrofitApi::class.java) }

    private fun generateParamsMap(text: String, sourceLang: Language, transLang: Language): Map<String, String> {
        val res = params.toMutableMap()
        res["sl"] = langIds[sourceLang]?: ""
        res["tl"] = langIds[transLang]?: ""
        res["q"] = text
        return res
    }

    override suspend fun translate(
        text: String,
        sourceLang: Language,
        transLang: Language
    ): RepoResult<String> {
        if (SystemStates.isInternetAvailable() != true) return ErrorResults.RepoRes.NO_INTERNET_CONNECTION
        val res = RetrofitResult.wrapper { retrofit.postFunctionFetchJson(endpoint = endpoint, params = generateParamsMap(text, sourceLang, transLang)) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonArray.get(0).asJsonArray.get(0).asJsonArray.get(0).asString)
        }
    }
}