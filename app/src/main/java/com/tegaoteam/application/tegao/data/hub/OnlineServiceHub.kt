package com.tegaoteam.application.tegao.data.hub

import android.net.Uri
import com.tegaoteam.application.tegao.data.network.appserver.OnlineServiceApi
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import timber.log.Timber
import java.io.File

class OnlineServiceHub {
    private val api = OnlineServiceApi.api

    suspend fun requestImageOCR(imageUri: Uri): RepoResult<List<String>> {
        val imagePath = imageUri.path
        Timber.i("Hub received URI from request with pathname = $imagePath")
        if (imagePath == null) return RepoResult.Error<Nothing>(404, "No file found")
        val imageFile = File(imageUri.path!!)
        return api.requestImageOCR(imageFile)
    }
}