package com.tegaoteam.application.tegao.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.tegaoteam.application.tegao.R

object TermBank {
    private lateinit var appContext: Context
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun askCC(resId: Int) = ContextCompat.getString(appContext, resId)
    private val termMap = mapOf<String, String>(
        "source" to askCC(R.string.term_source),
        "lang" to askCC(R.string.term_lang)
    )
    fun getByKey(key: String) = termMap[key]
}