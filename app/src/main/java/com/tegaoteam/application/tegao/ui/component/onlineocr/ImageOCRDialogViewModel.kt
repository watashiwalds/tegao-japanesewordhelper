package com.tegaoteam.application.tegao.ui.component.onlineocr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import timber.log.Timber

class ImageOCRDialogViewModel: ViewModel() {
    var isRecognizingSuccessful = false
        private set

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText
    fun requestImageOCR() {
        Timber.w("TODO: Write the function to call for ImageOCR Retrofit API")
    }

    val hasRecognizedText = _recognizedText.map { text -> text.isNotEmpty() && isRecognizingSuccessful }
}