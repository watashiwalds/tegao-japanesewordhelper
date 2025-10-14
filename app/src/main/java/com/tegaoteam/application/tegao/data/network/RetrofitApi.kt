package com.tegaoteam.application.tegao.data.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RetrofitApi {
    /**
     * If no body needed, pass it as JsonObject()
     */
    @POST("{endpoint}")
    suspend fun fetchJsonObject(
        @Path("endpoint") endpoint: String = "",
        @QueryMap params: Map<String, String>,
        @Body body: JsonObject
    ): JsonObject
}