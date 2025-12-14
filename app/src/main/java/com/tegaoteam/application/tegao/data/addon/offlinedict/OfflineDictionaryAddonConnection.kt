package com.tegaoteam.application.tegao.data.addon.offlinedict

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.tegaoteam.addon.tegao.yomitandictionary.ILookupCallback
import com.tegaoteam.addon.tegao.yomitandictionary.ILookupService
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.data.config.AddonConfig
import com.tegaoteam.application.tegao.data.config.DictionaryConfig
import com.tegaoteam.application.tegao.data.model.FlowStream
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.domain.interf.OfflineDictionaryApi
import com.tegaoteam.application.tegao.domain.model.Dictionary
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class OfflineDictionaryAddonConnection private constructor(context: Context): OfflineDictionaryApi {
    companion object {
        val instance by lazy { OfflineDictionaryAddonConnection(TegaoApplication.instance.applicationContext) }
        const val DICTIONARY_ID = "yomitan"
    }

    private var lookupService: ILookupService? = null
    private var lookupCallback: ILookupCallback? = null
    private val packageName = AddonConfig.offlineDictionaryPackagePath

    private val conn = object: ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            if (p0?.packageName != packageName) {
                Timber.e("Not the expected packageName for YomitanDictionary addon! [${p0?.packageName}]")
                p1?.let { context.unbindService(this) }
            }
            lookupService = ILookupService.Stub.asInterface(p1)
            if (lookupCallback != null) lookupService?.registerCallback(lookupCallback)
            Timber.i("Linked with YomitanDictionary addon")
            Timber.i("Register callback on service connecting $lookupCallback when service $lookupService")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            lookupService = null
            Timber.i("Unlinked with YomitanDictionary addon")
        }
    }

    init {
        Timber.i("Start binding with YomitanDictionary addon")
        context.bindService(
            Intent().setComponent(ComponentName(packageName, "$packageName.LookupService")),
            conn,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun registerCallback(callback: (String) -> Unit) {
        lookupCallback = object: ILookupCallback.Stub() {
            override fun onResult(rawResult: String?) {
                callback.invoke(rawResult?: "")
                Timber.i("Receive callback result: $rawResult")
            }
        }
        lookupService?.registerCallback(lookupCallback)
        Timber.i("Register callback on service connecting $lookupCallback when service $lookupService")
    }

    //region DictionaryLookupApi overrides
    override val dict: Dictionary? = DictionaryConfig.getDictionariesList().find { it.id == DICTIONARY_ID }

    override suspend fun searchWord(keyword: String): FlowStream<RepoResult<Any>> = FlowStream( callbackFlow{
        trySend(RepoResult.Success("$keyword..."))
        registerCallback { rawResult -> trySend(RepoResult.Success(rawResult)) }
        lookupService?.requestLookupResult(0, keyword)
        awaitClose { registerCallback {  } }
    } )

    override suspend fun searchKanji(keyword: String): FlowStream<RepoResult<Any>> = FlowStream( callbackFlow{
        trySend(RepoResult.Success("$keyword..."))
        registerCallback { rawResult -> trySend(RepoResult.Success(rawResult)) }
        lookupService?.requestLookupResult(1, keyword)
        awaitClose { registerCallback {  } }
    } )
    //endregion
}