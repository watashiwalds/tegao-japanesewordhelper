package com.tegaoteam.application.tegao.data.network

import com.tegaoteam.application.tegao.domain.independency.RepoResult

object ErrorResults {
    val NO_INTERNET_CONNECTION = RepoResult.Error<Nothing>(638, "No internet available")
    val EMPTY_RESULT = RepoResult.Error<Nothing>(378, "Empty result")
    val EMPTY_INPUT = RepoResult.Error<Nothing>(378, "Input data doesn't exist")
}