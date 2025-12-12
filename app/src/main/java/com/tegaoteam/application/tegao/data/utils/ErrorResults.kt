package com.tegaoteam.application.tegao.data.utils

import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.utils.getStringFromAppRes

object ErrorResults {
    object RepoRes {
        val NO_INTERNET_CONNECTION = RepoResult.Error<Nothing>(638,
            getStringFromAppRes(R.string.err_repo_noInternet)
        )
        val EMPTY_RESULT = RepoResult.Error<Nothing>(378,
            getStringFromAppRes(R.string.err_repo_emptyResult)
        )
        val EMPTY_INPUT = RepoResult.Error<Nothing>(378,
            getStringFromAppRes(R.string.err_repo_emptyInput)
        )
        val LINK_ERROR = RepoResult.Error<Nothing>(378,
            getStringFromAppRes(R.string.err_repo_linkError)
        )
    }
    object DictionaryRes {
        const val EMPTY_RESULT = 0
        const val PARSING_ERROR = 1

        fun wordRes(errorType: Int, msg: String? = null): List<Word> {
            var message = when (errorType) {
                EMPTY_RESULT -> getStringFromAppRes(R.string.err_dict_emptyResult)
                PARSING_ERROR -> getStringFromAppRes(R.string.err_dict_parsingError)
                else -> ""
            }
            return listOf(
                Word(
                    id = "-1",
                    reading = "",
                    furigana = listOf("$message ${msg ?: ""}"),
                    definitions = listOf()
                )
            )
        }

        fun kanjiRes(errorType: Int, msg: String? = null): List<Kanji> {
            var message = when (errorType) {
                EMPTY_RESULT -> getStringFromAppRes(R.string.err_dict_emptyResult)
                PARSING_ERROR -> getStringFromAppRes(R.string.err_dict_parsingError)
                else -> ""
            }
            return listOf(
                Kanji(
                    id = "-1",
                    character = "",
                    meaning = "$message ${msg ?: ""}"
                )
            )
        }
    }
}