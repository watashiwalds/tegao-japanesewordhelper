package com.tegaoteam.application.tegao.data.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitApi {
    @POST("{endpoint}{parameter}")
    suspend fun fetchJsonObject(
        @Path("endpoint") endpoint: String = "",
        @Path("parameter") parameter: String = "",
        @Body body: String
    ): JsonObject
}