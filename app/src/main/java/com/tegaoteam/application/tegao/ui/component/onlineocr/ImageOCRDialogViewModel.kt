package com.tegaoteam.application.tegao.ui.component.onlineocr

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.ui.account.SignInHelper
import com.tegaoteam.application.tegao.utils.EventBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ImageOCRDialogViewModel(private val _onlineServiceHub: OnlineServiceHub): ViewModel() {
    var selectedImageUri: Uri? = null
        private set
    var isRecognizingSuccessful = false
        private set

    val evRecognitionFailed = EventBeacon()

    private val _recognizedText = MutableLiveData<List<String>>()
    val recognizedText: LiveData<List<String>> = _recognizedText
    fun requestImageOCR(imageUri: Uri) {
        selectedImageUri = imageUri
        Timber.i("Receive request to OCR an image, sending to network...")
        SignInHelper.getUserToken { userToken ->
            viewModelScope.launch(Dispatchers.IO) {
                //TODO: lowerResolution as a setting value
                val res = _onlineServiceHub.requestImageOCR(userToken?: "", imageUri, true)
                withContext(Dispatchers.Main) {
                    when (res) {
                        is RepoResult.Success<List<String>> -> {
                            Timber.i("OCR success with data = ${res.data}")
                            isRecognizingSuccessful = true
                            _recognizedText.value = res.data
                        }
                        is RepoResult.Error<*> -> {
                            Timber.i("OCR failed with message = ${res.message}")
                            isRecognizingSuccessful = false
                            evRecognitionFailed.ignite("${res.code} ${res.message}")
                        }
                    }
                }
            }
        }
    }

    val hasRecognizedText = _recognizedText.map { text -> text.isNotEmpty() && isRecognizingSuccessful }

    companion object {
        class ViewModelFactory(
            private val onlineServiceHub: OnlineServiceHub
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ImageOCRDialogViewModel::class.java)) {
                    return ImageOCRDialogViewModel(onlineServiceHub) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}