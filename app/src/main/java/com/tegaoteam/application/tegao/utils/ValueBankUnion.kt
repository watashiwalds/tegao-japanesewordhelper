package com.tegaoteam.application.tegao.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.content.ContextCompat
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.domain.model.Translator

object TermBank {
    private var appContext: Context = TegaoApplication.instance.applicationContext

    private fun askCC(resId: Int): String {
        return try {
            ContextCompat.getString(appContext, resId)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private val highlightTerms = listOf("source", "jlpt", "frequency")
    fun getTermColor(key: String): Pair<Int, Int> {
        return when (key) {
            in highlightTerms -> Pair(ContextCompat.getColor(appContext, R.color.secondary_dark), ContextCompat.getColor(appContext, R.color.const_black))
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
        "tips" to "Mẹo",
        "definitionLabel" to "Phân loại",
        "info" to "Thông tin thêm",
        "restrictions" to "Hạn chế",
        "related" to "Từ vựng liên quan"
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
        "jlpt" to "Cấp độ xếp loại theo thang N-level tiếng Nhật",
        "alternateReadings" to "Các dạng khác của từ vựng này",
        "isCommon" to "Từ vựng được sử dụng thường xuyên",
        "wanikani" to "Xuất hiện trong bộ từ vựng theo cấp độ của Wanikani"
    )
    fun getTermDescription(key: String) = termDescriptions[key]?: ""
}

object LabelBank {
    private val langNames = mapOf(
        Translator.Companion.Language.JAPANESE to getStringFromAppRes(R.string.language_japanese),
        Translator.Companion.Language.VIETNAMESE to getStringFromAppRes(R.string.language_vietnamese),
        Translator.Companion.Language.ENGLISH to getStringFromAppRes(R.string.language_english)
    )
    fun getLanguageName(languageEnum: Translator.Companion.Language?): String? = langNames[languageEnum]
}

fun getStringFromAppRes(resId: Int): String {
    return ContextCompat.getString(TegaoApplication.instance, resId)
}
fun getDrawableFromAppRes(resId: Int): Drawable? {
    return ContextCompat.getDrawable(TegaoApplication.instance, resId)
}
fun getColorFromAppRes(resId: Int): Int {
    return ContextCompat.getColor(TegaoApplication.instance, resId)
}