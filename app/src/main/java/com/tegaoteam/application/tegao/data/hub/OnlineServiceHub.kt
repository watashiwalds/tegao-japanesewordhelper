package com.tegaoteam.application.tegao.data.hub

import android.net.Uri
import com.tegaoteam.application.tegao.data.network.appserver.OnlineServiceApi
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.utils.UriHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

class OnlineServiceHub {
    private val api = OnlineServiceApi.api

    private fun inputStreamToRequestBody(uri: Uri) = object : RequestBody() {
        override fun contentType() = "image/*".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            UriHelper.getUriInputStream(uri)?.use { stream ->
                stream.source().use { source ->
                    sink.writeAll(source)
                }
            }
        }
    }

    suspend fun requestImageOCR(imageUri: Uri): RepoResult<List<String>> {
        val requestBody = inputStreamToRequestBody(imageUri)
        val imageExt = UriHelper.getUriFileExtension(imageUri)
        return api.requestImageOCR(requestBody, imageExt)
    }
}