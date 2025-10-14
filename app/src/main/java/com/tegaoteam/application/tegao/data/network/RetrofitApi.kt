package com.tegaoteam.application.tegao.data.network

import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface RetrofitApi {
    /**
     * If no body needed, pass it as JsonObject()
     */
    @POST("{endpoint}")
    suspend fun postFunctionFetchJson(
        @Path("endpoint") endpoint: String = "",
        @QueryMap params: Map<String, String>,
        @Body body: JsonObject
    ): JsonObject

    @GET("{endpoint}")
    suspend fun getFunctionFetchJson(
        @Path("endpoint") endpoint: String = "",
        @QueryMap params: Map<String, String>,
        @Body body: JsonObject
    ): JsonObject
}