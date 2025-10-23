package com.tegaoteam.application.tegao.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

fun JsonObject.toMap(): Map<String, Any> {
    val typeToken = object: TypeToken<Map<String, Any>>() {}.type
    return Gson().fromJson(this, typeToken)
}

fun String.toSafeQueryString(): String {
    val regex = "[^\\p{L}\\p{Nd}\\s]+"
    return this.replace(Regex(regex), "")
}