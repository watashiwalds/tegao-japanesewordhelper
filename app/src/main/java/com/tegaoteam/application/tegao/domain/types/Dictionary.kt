package com.tegaoteam.application.tegao.domain.types

import com.google.gson.Gson
import com.google.gson.JsonObject

class Dictionary(
    val id: Int,
    val displayName: String,
    val type: Int,
    jsonInfos: String
) {
    //parse to default JsonObject (JsonObject::class.java)
    var jsonObject: JsonObject = Gson().fromJson(jsonInfos, JsonObject::class.java)
}