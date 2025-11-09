package com.tegaoteam.application.tegao.data.addon

import android.graphics.Bitmap
import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi

class HandwritingAddonApi private constructor(): AlternativeInputApi {

    //todo: link function to actual handwriting addon after complete the addon apk
    override suspend fun requestInputSuggestions(input: Any?): List<String> {
        if (input !is Bitmap) return listOf()
        //for display testing
        return getSomeRandomChars()
    }

    companion object {
        //for testing
        private val randomChars = listOf<Char>('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
        private val randomNumbers = listOf<Int>(0,1,2,3,4,5,6,7,8,9,10)
        private fun getSomeRandomChars(): List<String> {
            val result = mutableListOf<String>()
            for (i in 0..randomNumbers.random()) result.add(randomChars.random().toString())
            return result
        }

        val instance by lazy { HandwritingAddonApi() }
    }
}