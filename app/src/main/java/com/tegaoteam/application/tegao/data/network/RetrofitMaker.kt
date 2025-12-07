package com.tegaoteam.application.tegao.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Make Retrofit client with 1 shared OkHttpClient for reduced connection load
 */
object RetrofitMaker {
    //logging interceptor for url call logging
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Logs URL, headers, and body
    }

    //shared OkHttpClient for all Retrofit instance of each source
    private val browsingOkHttp by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .callTimeout(5, TimeUnit.SECONDS)
//            .addInterceptor(logging)
            .build()
    }

    private val processingOkHttp by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS)
            .writeTimeout(0, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.SECONDS)
//            .addInterceptor(logging)
            .build()
    }

    /**
     * Make a new Retrofit client connected to the link of ```url``` value
     */
    fun createWithUrl(url: String, type: Int): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(if (!url.isBlank()) url else "http://example.com")
            when (type) {
                TYPE_BROWSING -> client(browsingOkHttp)
                TYPE_PROCESSING -> client(processingOkHttp)
            }
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    const val TYPE_BROWSING = 0
    const val TYPE_PROCESSING = 1
}