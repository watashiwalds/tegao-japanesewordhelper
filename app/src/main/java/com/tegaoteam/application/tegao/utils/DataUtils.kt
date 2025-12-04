package com.tegaoteam.application.tegao.utils

import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.security.MessageDigest
import java.time.Instant
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException
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
    private fun transformToTimeType(value: Any?): LocalDateTime? {
        return try {
            when (value) {
                is String -> LocalDateTime.parse(value)
                is LocalDateTime -> value
                else -> null
            }
        } catch (e: Exception) {
            return if (e is DateTimeParseException)
                Instant.parse(value as String).atZone(ZoneId.systemDefault()).toLocalDateTime()
            else
                null
        }
    }
    fun getCurrentTimeInSeconds() = getCurrentTimestamp().toEpochSecond(ZoneId.systemDefault().rules.getOffset(getCurrentTimestamp()))
    fun getCurrentTimestamp(): LocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
    fun getTodayMidnightTimestamp(): LocalDateTime = LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime()
    fun preciseTimeDifferenceBetween(start: Any?, end: Any?, differenceIn: Int): Long {
        val cStart = transformToTimeType(start)
        val cEnd = transformToTimeType(end)
        val durationDiff = Duration.between(cStart, cEnd)
        return when (differenceIn) {
            DIFF_DAY -> durationDiff.toDays()
            else -> 0
        }
    }
    fun absoluteTimeDifferenceBetween(start: Any?, end: Any?, differenceIn: Int): Long {
        val cStart = transformToTimeType(start)
        val cEnd = transformToTimeType(end)
        return when (differenceIn) {
            DIFF_DAY -> ChronoUnit.DAYS.between(cStart, cEnd)
            else -> 0
        }
    }
    const val DIFF_DAY = 0

    fun addDays(dateTime: Any, days: Long): LocalDateTime? {
        val dt = transformToTimeType(dateTime)
        return dt?.plus(Duration.ofDays(days))
    }
}

fun getMD5HashedValue(input: Any): String {
    val messageDigester = MessageDigest.getInstance("MD5")
    val hashedBytes = messageDigester.digest(input.toString().toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}

fun dpToPixel(value: Float): Float {
    return value * Resources.getSystem().displayMetrics.density
}