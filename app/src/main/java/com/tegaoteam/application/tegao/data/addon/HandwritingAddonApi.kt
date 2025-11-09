package com.tegaoteam.application.tegao.data.addon

import android.graphics.Bitmap
import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi
import java.io.ByteArrayOutputStream

class HandwritingAddonApi private constructor(): AlternativeInputApi {

    //todo: link function to actual handwriting addon after complete the addon apk
    override suspend fun requestInputSuggestions(input: Any?): List<String> {
        if (input !is Bitmap) return listOf()

        //for display testing
        val imgCompressed = ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.PNG, 100, imgCompressed)
        return getSomeRandomChars().apply { add(imgCompressed.size().toString()) }
    }

    companion object {
        //for testing
        private val randomChars = listOf<Char>('漢','字','梵','語','千','文','鬘','唐','聖','照','権','実','鏡','弘','仁','真','名','仮','平','万','葉','子','供','煙','草','天')
        private val randomNumbers = listOf<Int>(0,1,2,3,4,5,6,7,8,9,10)
        private fun getSomeRandomChars(): MutableList<String> {
            val result = mutableListOf<String>()
            for (i in 0..randomNumbers.random()) result.add(randomChars.random().toString())
            return result
        }

        val instance by lazy { HandwritingAddonApi() }
    }
}