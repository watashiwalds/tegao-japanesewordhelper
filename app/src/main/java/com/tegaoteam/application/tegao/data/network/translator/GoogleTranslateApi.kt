package com.tegaoteam.application.tegao.data.network.translator

import com.tegaoteam.application.tegao.data.config.TranslatorConfig
import com.tegaoteam.application.tegao.data.network.RetrofitApi
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.domain.model.Translator.Companion.Language as Language

class GoogleTranslateApi private constructor(): TranslatorApi{
    companion object {
        val api by lazy { GoogleTranslateApi() }

        private val rootUrl = "https://translate.googleapis.com/translate_a/"
        private val paramsUrl = "single?client=gtx&sl=%s&tl=%s&dt=t&q=%s"

        private val langIds = mapOf<Language, String>(
            Language.JAPANESE to "ja",
            Language.VIETNAMESE to "vi",
            Language.ENGLISH to "en"
        )
    }

    override val translator = TranslatorConfig.TRANSLATOR_GOOGLETRANSLATE
    private val retrofit by lazy { RetrofitMaker.createWithUrl(rootUrl).create(RetrofitApi::class.java) }

    private fun generateParamsUrl(text: String, sourceLang: Language, transLang: Language): String {
        return String.format(paramsUrl, langIds[sourceLang], langIds[transLang], text)
    }

    override suspend fun translate(
        text: String,
        sourceLang: Language,
        transLang: Language
    ): RepoResult<String> {
        val res = RetrofitResult.wrapper { retrofit.postFunctionFetchJson(endpoint = generateParamsUrl(text, sourceLang, transLang)) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> RepoResult.Success(res.data.asJsonArray.get(0).asJsonArray.get(0).asJsonArray.get(0).asString)
        }
    }

    override fun onNoInternetAvailable(): RepoResult<Nothing> {
        return RepoResult.Error<Nothing>(-1, "No internet available")
    }
}