package com.tegaoteam.application.tegao.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication

object TermBank {
    private var appContext: Context = TegaoApplication.instance.applicationContext

    private fun askCC(resId: Int): String {
        return try {
            ContextCompat.getString(appContext, resId)
        } catch (e: Exception) {
            e.toString()
        }
    }

    //TODO: Chuyển qua string resource để làm multi-language
    private val termLabels = mapOf(
        "source" to "Nguồn",
        "lang" to "Ngôn ngữ",
        "kunyomi" to "Ku",
        "onyomi" to "On",
        "composite" to "Bộ",
        "detail" to "Chi tiết",
        "example" to "Ví dụ",
        "tips" to "Mẹo"
    )
    fun getTermLabel(key: String) = termLabels[key]?: key

    private val termDescriptions = mapOf(
        "source" to askCC(R.string.term_source),
        "lang" to askCC(R.string.term_lang)
    )
    fun getTermDescription(key: String) = termDescriptions[key]?: key
}