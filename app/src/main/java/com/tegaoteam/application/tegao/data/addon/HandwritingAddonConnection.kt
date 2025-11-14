package com.tegaoteam.application.tegao.data.addon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import com.tegaoteam.addon.tegao.handwritingrecognition.IRecognitionCallback
import com.tegaoteam.addon.tegao.handwritingrecognition.IRecognitionService
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi
import timber.log.Timber
import java.io.ByteArrayOutputStream

class HandwritingAddonConnection private constructor (context: Context): AlternativeInputApi {
    private var recognitionService: IRecognitionService? = null
    private var recognitionCallback: IRecognitionCallback? = null
    private val packageName = AddonConfig.handwritingPackagePath

    private val conn = object: ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            if (p0?.packageName != packageName) {
                Timber.e("Not the expected packageName for HandwritingRecognition addon! [${p0?.packageName}]")
                p1?.let { context.unbindService(this) }
            }
            recognitionService = IRecognitionService.Stub.asInterface(p1)
            if (recognitionCallback != null) recognitionService?.registerCallback(recognitionCallback)
            Timber.i("Linked with HandwritingRecognition addon")
            Timber.i("Callback registered $recognitionCallback when service $recognitionService")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            recognitionService = null
            Timber.i("Unlinked with HandwritingRecognition addon")
        }
    }

    init {
        Timber.i("Start binding with HandwritingRecognition addon")
        context.bindService(
            Intent().setComponent(ComponentName(packageName, "$packageName.RecognitionService")),
            conn,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun requestInputSuggestions(input: Any?) {
        if (recognitionCallback == null) {
            Timber.w("No callback present, this request is promptly rejected. Do .registerCallback() properly to perform this action.")
            return
        }
        if (input !is Bitmap) {
            Timber.w("Not Bitmap wanting to request suggestion. The model accept only Bitmap")
            return
        }

        Timber.w("Requesting for new suggestion... Model ready: ${recognitionService != null}")
        val imgCompressed = ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.PNG, 100, imgCompressed)
        recognitionService?.requestInputSuggestions(imgCompressed.toByteArray())
    }

    override fun registerCallback(callback: (List<String>) -> Unit) {
        recognitionCallback = object: IRecognitionCallback.Stub() {
            override fun onRecognized(suggestions: Array<String>) {
                callback.invoke(suggestions.toList())
                Timber.i("Receive callback result: ${suggestions.toList()}")
            }
        }
        recognitionService?.registerCallback(recognitionCallback)
        Timber.i("Callback registered $callback when service $recognitionService")
    }

    companion object {
        val instance by lazy { HandwritingAddonConnection(TegaoApplication.instance.applicationContext) }
    }
}