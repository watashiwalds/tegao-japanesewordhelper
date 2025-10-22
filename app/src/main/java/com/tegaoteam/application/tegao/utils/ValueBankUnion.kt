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
    private val termMap = mapOf(
        "source" to askCC(R.string.term_source),
        "lang" to askCC(R.string.term_lang)
    )
    fun getTerm(key: String) = termMap[key]?: key
}