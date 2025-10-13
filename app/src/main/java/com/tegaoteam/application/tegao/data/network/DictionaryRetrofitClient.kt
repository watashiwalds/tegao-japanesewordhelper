package com.tegaoteam.application.tegao.data.network

import com.tegaoteam.application.tegao.domain.type.Dictionary

class DictionaryRetrofitClient(val dict: Dictionary?) {
    val retrofit by lazy {
        if (dict != null) RetrofitMaker.createWithUrl(dict.jsonObject.get(Dictionary.ONL_URL).asString) else null
    }
}