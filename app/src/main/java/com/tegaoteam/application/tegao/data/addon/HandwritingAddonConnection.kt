package com.tegaoteam.application.tegao.data.addon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import com.tegaoteam.addon.tegao.handwritingrecognition.IRecognitionService
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.domain.interf.AlternativeInputApi
import timber.log.Timber
import java.io.ByteArrayOutputStream

class HandwritingAddonConnection private constructor (context: Context): AlternativeInputApi {
    private var recognitionService: IRecognitionService? = null
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
            Timber.i("Linked with HandwritingRecognition addon")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Timber.i("Unlinked with HandwritingRecognition addon")
        }
    }

    init {
        context.bindService(
            Intent().setComponent(ComponentName(packageName, "$packageName.RecognitionService")),
            conn,
            Context.BIND_AUTO_CREATE
        )
    }

    //todo: link function to actual handwriting addon after complete the addon apk
    override suspend fun requestInputSuggestions(input: Any?): List<String> {
        if (input !is Bitmap) return listOf()

        val imgCompressed = ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.PNG, 100, imgCompressed)

        val result = recognitionService?.requestInputSuggestions(imgCompressed.toByteArray())
        var transformedResult = listOf<String>()
        result?.let { transformedResult = it.toList() }
        return transformedResult
    }

    companion object {
        val instance by lazy { HandwritingAddonConnection(TegaoApplication.instance.applicationContext) }
    }
}