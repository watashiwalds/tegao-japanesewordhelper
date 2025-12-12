package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.data.utils.ErrorResults
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import timber.log.Timber

class OnlineServiceResultParser {
    fun toRecognizedOCRResults(json: JsonElement): RepoResult<List<String>> {
        Timber.i("Parser receive Element from API with data status: ${!json.isJsonNull}")
        val parseRes = mutableListOf<String>()
        try {
            val dataList = json.asJsonObject.get("data").asJsonArray.map { it.asJsonObject }
            val stringList = dataList.mapNotNull { dt -> dt.get("text").takeUnless { it.isJsonNull }?.asString }
            parseRes.addAll(stringList)
        } catch (_: Exception) {
            return RepoResult.Error<Nothing>(777, "Parsing Error")
        }
        return if (parseRes.isNotEmpty())
            RepoResult.Success<List<String>>(parseRes)
        else
            ErrorResults.RepoRes.EMPTY_RESULT
    }
}