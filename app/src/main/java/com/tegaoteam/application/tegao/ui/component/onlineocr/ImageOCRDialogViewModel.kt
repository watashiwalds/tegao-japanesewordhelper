package com.tegaoteam.application.tegao.ui.component.onlineocr

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import timber.log.Timber

class ImageOCRDialogViewModel: ViewModel() {
    var selectedImageUri: Uri? = null
        private set

    var isRecognizingSuccessful = false
        private set

    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText
    fun requestImageOCR(imageUri: Uri) {
        selectedImageUri = imageUri
        Timber.w("TODO: Write the function to call for ImageOCR Retrofit API")
        isRecognizingSuccessful = true
        _recognizedText.value = "Devtest value for frontend functions"
    }

    val hasRecognizedText = _recognizedText.map { text -> text.isNotEmpty() && isRecognizingSuccessful }
}