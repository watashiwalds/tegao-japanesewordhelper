package com.tegaoteam.application.tegao.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Make Retrofit client with 1 shared OkHttpClient for reduced connection load
 */
object RetrofitMaker {
    //logging interceptor for url call logging
//    val logging = HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY  // Logs URL, headers, and body
//    }

    //shared OkHttpClient for all Retrofit instance of each source
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
//            .addInterceptor(logging)
            .build()
    }

    /**
     * Make a new Retrofit client connected to the link of ```url``` value
     */
    fun createWithUrl(url: String): Retrofit {
//        //TODO: Barebone Moshi, need more configuration
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()

        return Retrofit.Builder()
            .baseUrl(if (!url.isBlank()) url else "http://example.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}