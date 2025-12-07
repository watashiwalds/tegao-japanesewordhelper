package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.config.SystemStates
import com.tegaoteam.application.tegao.data.network.ErrorResults
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import kotlin.math.min

class OnlineServiceApi private constructor() {
    private val retrofit: TegaoFirebaseApi by lazy { RetrofitMaker.createWithUrl(TegaoFirebaseConst.rootUrl).create(TegaoFirebaseApi::class.java) }
    private val parser = OnlineServiceResultParser()

    companion object {
        val api by lazy { OnlineServiceApi() }
    }

    suspend fun requestImageOCR(image: RequestBody, imageExtension: String? = null): RepoResult<List<String>> {
        if (!SystemStates.isInternetAvailable()!!) return ErrorResults.NO_INTERNET_CONNECTION

        val partParsing = MultipartBody.Part.createFormData(
            "file",
            imageExtension?.let { "requestedOCR.$it" }?: "requestedOCR",
            image
        )
        Timber.i("API received File from Hub request with body = ${partParsing.body.toString().let { it.substring(0, min(50, it.length)) }}")
        val res = RetrofitResult.wrapper { retrofit.postImageOCR(TegaoFirebaseConst.obsoleteSoon, partParsing) }
        Timber.i("API finish recognizing")
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> parser.toRecognizedOCRResults(res.data)
        }
    }
}