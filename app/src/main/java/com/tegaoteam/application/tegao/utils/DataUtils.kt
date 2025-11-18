package com.tegaoteam.application.tegao.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest
import java.time.Instant
import java.time.Duration

fun JsonObject.toMap(): Map<String, Any> {
    val typeToken = object: TypeToken<Map<String, Any>>() {}.type
    return Gson().fromJson(this, typeToken)
}

fun String.toSafeQueryString(): String {
    val regex = "[^\\p{L}\\p{Nd}\\s]+"
    return this.replace(Regex(regex), "").trim()
}

object Time {
    fun getCurrentTimestamp(): Instant? = Instant.now()
    fun timeDifferenceBetween(start: Any?, end: Any?, differenceIn: Int): Long {
        val cStart = when (start) {
            is String -> Instant.parse(start)
            is Instant -> start
            else -> getCurrentTimestamp()
        }
        val cEnd = when (end) {
            is String -> Instant.parse(end)
            is Instant -> end
            else -> getCurrentTimestamp()
        }
        val durationDiff = Duration.between(cStart, cEnd)
        return when (differenceIn) {
            DIFF_DAY -> durationDiff.toDays()
            else -> durationDiff.toMinutes()
        }
    }

    const val DIFF_DAY = 0
}

fun getMD5HashedValue(input: Any): String {
    val messageDigester = MessageDigest.getInstance("MD5")
    val hashedBytes = messageDigester.digest(input.toString().toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}