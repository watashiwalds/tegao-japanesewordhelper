package com.tegaoteam.application.tegao.data.hub

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import com.tegaoteam.application.tegao.data.network.ErrorResults
import com.tegaoteam.application.tegao.data.network.appserver.OnlineServiceApi
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.utils.FileHelper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream

class OnlineServiceHub {
    private val api = OnlineServiceApi.api

    private fun inputStreamToRequestBody(uri: Uri) = object : RequestBody() {
        override fun contentType() = "image/*".toMediaType()
        override fun writeTo(sink: BufferedSink) {
            FileHelper.getUriInputStream(uri)?.use { stream ->
                stream.source().use { source ->
                    sink.writeAll(source)
                }
            }
        }
    }

    private fun bitmapToRequestBody(bitmap: Bitmap): RequestBody {
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteStream)
        return byteStream.toByteArray().toRequestBody("image/jpeg".toMediaType())
    }

    private fun downscaledByteArrayAsOCRInput(bitmap: Bitmap): Bitmap {
        val maxSize = 512
        val w = bitmap.width
        val h = bitmap.height
        val scale = if (w >= h) {
            maxSize.toFloat() / w
        } else {
            maxSize.toFloat() / h
        }
        val newW = (w * scale).toInt()
        val newH = (h * scale).toInt()
        return bitmap.copy(Bitmap.Config.ARGB_8888, false).scale(newW, newH)
    }

    suspend fun requestImageOCR(imageUri: Uri, lowerResolution: Boolean): RepoResult<List<String>> {
        val sourceBitmap = FileHelper.getBitmapFromUri(imageUri)
        if (sourceBitmap == null) return ErrorResults.RepoRes.EMPTY_INPUT

        val inpBitmap = if (lowerResolution) downscaledByteArrayAsOCRInput(sourceBitmap) else sourceBitmap
        val requestBody = bitmapToRequestBody(inpBitmap)
        return api.requestImageOCR(requestBody, "jpeg")
    }
}