package com.tegaoteam.application.tegao.data.network.appserver.cardpack

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface CardSharingApiInterface {
    @GET
    suspend fun getJson(@Url link: String): Response<JsonElement>
}