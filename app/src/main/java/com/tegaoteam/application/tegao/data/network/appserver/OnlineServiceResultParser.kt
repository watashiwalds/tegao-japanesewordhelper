package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.tegaoteam.application.tegao.domain.independency.RepoResult

class OnlineServiceResultParser {
    fun toRecognizedOCRResults(json: JsonElement): RepoResult<List<String>> {
        val parseRes = mutableListOf<String>()
        try {
            val dataList = json.asJsonObject.get("data").asJsonArray.map { it.asJsonObject }
            val stringList = dataList.mapNotNull { dt -> dt.get("text").takeUnless { it.isJsonNull }?.asString }
            parseRes.addAll(stringList)
        } catch (_: Exception) {
            return RepoResult.Error<Nothing>(777, "Parsing Error")
        }
        return RepoResult.Success<List<String>>(parseRes)
    }
}