package com.tegaoteam.application.tegao.utils

import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.security.MessageDigest
import java.time.Instant
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun JsonObject.toMap(): Map<String, Any> {
    val typeToken = object: TypeToken<Map<String, Any>>() {}.type
    return Gson().fromJson(this, typeToken)
}

fun String.toSafeQueryString(): String {
    val regex = "[^\\p{L}\\p{Nd}\\s]+"
    return this.replace(Regex(regex), "").trim()
}

object Time {
    private fun transformToInstant(value: Any?): Instant? {
        return try {
            when (value) {
                is String -> Instant.parse(value)
                is Instant -> value
                else -> getCurrentTimestamp()
            }
        } catch (_: Exception) {
            getCurrentTimestamp()
        }
    }
    fun getCurrentTimestamp(): Instant = Instant.now()
    fun getTodayMidnightTimestamp(): Instant = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant()
    fun preciseTimeDifferenceBetween(start: Any?, end: Any?, differenceIn: Int): Long {
        val cStart = transformToInstant(start)
        val cEnd = transformToInstant(end)
        val durationDiff = Duration.between(cStart, cEnd)
        return when (differenceIn) {
            DIFF_DAY -> durationDiff.toDays()
            else -> 0
        }
    }
    fun absoluteTimeDifferenceBetween(start: Any?, end: Any?, differenceIn: Int): Long {
        val cStart = transformToInstant(start)
        val cEnd = transformToInstant(end)
        return when (differenceIn) {
            DIFF_DAY -> ChronoUnit.DAYS.between(cStart, cEnd)
            else -> 0
        }
    }
    const val DIFF_DAY = 0

    fun addDays(instant: Instant, days: Long) = instant.plus(Duration.ofDays(days))
}

fun getMD5HashedValue(input: Any): String {
    val messageDigester = MessageDigest.getInstance("MD5")
    val hashedBytes = messageDigester.digest(input.toString().toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}

fun dpToPixel(value: Float): Float {
    return value * Resources.getSystem().displayMetrics.density
}