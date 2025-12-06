package com.tegaoteam.application.tegao.data.network

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
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
        @QueryMap params: Map<String, String> = mapOf(),
        @Body body: JsonObject = JsonObject()
    ): Response<JsonObject>

    @GET("{endpoint}")
    suspend fun getFunctionFetchJson(
        @Path("endpoint", encoded = true) endpoint: String = "",
        @QueryMap params: Map<String, String> = mapOf()
    ): Response<JsonObject>

    @GET("{endpoint}")
    suspend fun getFunctionFetchRaw(
        @Path("endpoint", encoded = true) endpoint: String = "",
        @QueryMap params: Map<String, String> = mapOf()
    ): Response<ResponseBody>
}