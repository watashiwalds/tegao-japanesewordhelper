package com.tegaoteam.application.tegao.data.network

import com.tegaoteam.application.tegao.domain.independency.RepoResult
import retrofit2.Response

class RetrofitResult {
    companion object {
        suspend fun <T> wrapper(call: suspend () -> Response<T>): RepoResult<T> {
            return try {
                val res = call()
                if (res.isSuccessful && res.body() != null) {
                    RepoResult.Success(res.body()!!)
                } else {
                    RepoResult.Error<T>(code = res.code(), message = res.errorBody().toString())
                }
            } catch(e: Exception) {
                RepoResult.Error<T>(message = e.toString())
            }
        }
    }
}