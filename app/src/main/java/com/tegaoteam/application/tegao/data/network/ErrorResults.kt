package com.tegaoteam.application.tegao.data.network

import com.tegaoteam.application.tegao.domain.independency.RepoResult

object ErrorResults {
    val NO_INTERNET_CONNECTION = RepoResult.Error<Nothing>(-1, "No internet available")
}