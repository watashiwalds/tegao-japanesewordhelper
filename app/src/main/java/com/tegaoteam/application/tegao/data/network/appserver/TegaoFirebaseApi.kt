package com.tegaoteam.application.tegao.data.network.appserver

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TegaoFirebaseApi {
    @POST("login")
    suspend fun notifyLoginToken(
        @Body body: JsonObject = JsonObject()
    ): Response<JsonElement>

    @Multipart
    @POST("ocr")
    suspend fun postImageOCR(
        @Header("x-api-key") sessionKey: String,
        @Header("Authorization") token: String,
        @Part part: MultipartBody.Part
    ): Response<JsonElement>

    @POST("chat")
    suspend fun postChatbotConversation(
        @Header("x-api-key") sessionKey: String,
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Response<JsonElement>
}