package com.tegaoteam.application.tegao.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Make Retrofit client with 1 shared OkHttpClient for reduced connection load
 */
object RetrofitMaker {
    //shared OkHttpClient for all Retrofit instance of each source
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Make a new Retrofit client connected to the link of ```url``` value
     */
    fun createWithUrl(url: String): Retrofit {

        //TODO: Barebone Moshi, need more configuration
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}