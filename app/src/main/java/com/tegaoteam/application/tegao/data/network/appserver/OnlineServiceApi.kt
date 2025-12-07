package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.network.RetrofitMaker
import com.tegaoteam.application.tegao.data.network.RetrofitResult
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class OnlineServiceApi private constructor() {
    private val retrofit: TegaoFirebaseApi by lazy { RetrofitMaker.createWithUrl(TegaoFirebaseConst.rootUrl).create(TegaoFirebaseApi::class.java) }
    private val parser = OnlineServiceResultParser()

    companion object {
        val api by lazy { OnlineServiceApi() }
    }

    suspend fun requestImageOCR(image: File): RepoResult<List<String>> {
        val partParsing = MultipartBody.Part.createFormData(
            "file",
            image.name,
            image.asRequestBody("image/*".toMediaTypeOrNull())
        )
        val res = RetrofitResult.wrapper { retrofit.postImageOCR(TegaoFirebaseConst.obsoleteSoon, partParsing) }
        return when (res) {
            is RepoResult.Error<*> -> res
            is RepoResult.Success<JsonElement> -> parser.toRecognizedOCRResults(res.data)
        }
    }
}