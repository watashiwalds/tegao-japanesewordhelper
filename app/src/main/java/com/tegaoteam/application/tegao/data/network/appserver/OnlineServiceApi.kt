package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.tegaoteam.application.tegao.data.config.SystemStates
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import kotlin.math.min

class OnlineServiceApi private constructor() {
    private val retrofit: TegaoFirebaseApi by lazy { RetrofitMaker.createWithUrl(TegaoFirebaseConst.rootUrl, RetrofitMaker.TYPE_PROCESSING).create(TegaoFirebaseApi::class.java) }
    private val parser = OnlineServiceResultParser()

    companion object {
        val api by lazy { OnlineServiceApi() }
    }

    suspend fun notifyLoginTokenToServer(token: String): RepoResult<JsonElement> {
        val body = JsonObject().apply { addProperty("idToken", token) }
        Timber.d("${body}")
        val res = RetrofitResult.wrapper { retrofit.notifyLoginToken(body) }
        return res
    }

    suspend fun requestImageOCR(userToken: String, image: RequestBody, imageExtension: String? = null): RepoResult<List<String>> {
        if (SystemStates.isInternetAvailable() != true) return ErrorResults.RepoRes.NO_INTERNET_CONNECTION

        val partParsing = MultipartBody.Part.createFormData(
            "file",
            imageExtension?.let { "requestedOCR.$it" }?: "requestedOCR",
            image
        )
        Timber.i("API received File from Hub request, userId = $userToken")
        val res = RetrofitResult.wrapper { retrofit.postImageOCR(TegaoFirebaseConst.obsoleteSoon, userToken, partParsing) }
        Timber.i("API finish recognizing")
        return when (res) {
            is RepoResult.Error<*> -> RepoResult.Error<Nothing>(res.code, res.message)
            is RepoResult.Success<JsonElement> -> parser.toRecognizedOCRResults(res.data)
        }
    }
}