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

    private val yellowTerms = listOf("source", "jlpt", "frequency")
    fun getTermColor(key: String): Pair<Int, Int> {
        return when (key) {
            in yellowTerms -> Pair(ContextCompat.getColor(appContext, R.color.secondary), ContextCompat.getColor(appContext, R.color.const_black))
            else -> Pair(ContextCompat.getColor(appContext, R.color.neutral), ContextCompat.getColor(appContext, R.color.text_normal))
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
    fun getTermLabel(key: String) = termLabels[key]?: ""

    private val termDescriptions = mapOf(
        "source" to askCC(R.string.term_source),
        "lang" to askCC(R.string.term_lang),
        "kunyomi" to "Cách đọc thuần Nhật",
        "onyomi" to "Cách đọc theo âm Hán tự",
        "composite" to "Phân giải cấu trúc của Hán tự",
        "detail" to "Giải nghĩa của Hán tự theo lịch sử",
        "example" to "Mẫu câu có sử dụng từ vựng này",
        "tips" to "Cách nhớ nhanh không theo quy tắc",
        "frequency" to "Xếp hạng mức độ sử dụng trong văn bản tiếng Nhật thực tế",
        "stroke" to "Số nét tạo thành",
        "jlpt" to "Cấp độ xếp loại theo thang N-level tiếng Nhật"
    )
    fun getTermDescription(key: String) = termDescriptions[key]?: ""
}