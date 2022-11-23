package com.app.wildtreasure.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val app: Application) : BaseViewModel(app) {

    private val _data = MutableStateFlow<MainState>(MainState.Loading)
    val data = _data.asStateFlow()


    fun init(activity: MainActivity) {
        viewModelScope.launch(Dispatchers.IO) {

            //check ADB and if true =>
//            _data.emit(MainState.NavigateToGame)

//            if (!isNotADB()) {
//                _data.emit(MainState.NavigateToGame)
//            }

            val dataUrls = (app as App).dataBase.dataUrlDao().getAll()

            if (dataUrls.isNotEmpty()) {
                _data.emit(MainState.NavigateToWeb(dataUrls.first().uri))
            } else {
                val apps = getAppsflyer(activity)
                val deep = deepFlow(activity)
                _data.emit(MainState.FBState("Init step 1"))
                val adId = AdvertisingIdClient.getAdvertisingIdInfo(activity).id.toString()
                val uId = AppsFlyerLib.getInstance().getAppsFlyerUID(activity)!!
                _data.emit(MainState.FBState("Init step 2"))

                OneWrapper(app, adId).send(apps?.get("campaign").toString(), deep)

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
                _data.emit(MainState.NavigateToWeb(url))

            }
        }
    }

    private fun isNotADB(): Boolean =
        Settings.Global.getString(
            app.contentResolver,
            Settings.Global.ADB_ENABLED
        ) != "1"

    private suspend fun getAppsflyer(activity: MainActivity): MutableMap<String, Any>? =
        suspendCoroutine { coroutine ->

            val callback = object : AppsWrapper {

                override fun onConversionDataSuccess(convData: MutableMap<String, Any>?) {
                    coroutine.resume(convData)
                }

                override fun onConversionDataFail(p0: String?) {
                    coroutine.resume(null)
                }
            }
            AppsFlyerLib.getInstance().init(Const.APPS_FLYER_KEY, callback, activity)
            AppsFlyerLib.getInstance().start(activity)
        }

    private suspend fun deepFlow(activity: MainActivity): String =
        suspendCoroutine { coroutine ->
            val callback = AppLinkData.CompletionHandler {
                coroutine.resume(it?.targetUri.toString())
            }
            AppLinkData.fetchDeferredAppLinkData(activity, callback)
        }


    fun saveUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = (app as App).dataBase.dataUrlDao()
            val dataUrls = dao.getAll()
            if (dataUrls.isEmpty()) {
                Log.e("DATABASE", "save url = $url")
                dao.insert(DataUrl(0, url))
            } else {
//                val data = dataUrls.first()
//                val updatedData = data.copy(uri = url)
//                dao.update(updatedData)
            }
        }
    }


    // GAME PART------------------------------------------
    var position = MutableLiveData<Int>(0)
    var credits = MutableLiveData<Int>(110)

    private val resIdis = listOf<Int>(
        R.drawable.p1,
        R.drawable.p2,
        R.drawable.p3,
        R.drawable.p4,
        R.drawable.p5,
        R.drawable.p6,
        R.drawable.p7,
        R.drawable.p8,

        )

//    private val _dispalyCredits = MutableStateFlow<Int>(100)
//    val displayCredits = _dispalyCredits.asSharedFlow()

    val items = MutableLiveData<MutableList<Item>>(mutableListOf())

    init {
        addRandomSlot()
        Log.e("true", "Credits  ${credits.value}")

    }

    fun addPosition() {
        if (credits.value == 0) {

            return
        }
        addRandomSlot()
        checkSlot()
        position.value = position.value?.plus(4)
    }

    fun checkSlot() {
        val lastItems = items.value?.takeLast(4)

        val idis: MutableList<MutableList<Int>> = mutableListOf()
        lastItems?.forEach {
            val smallList = mutableListOf<Int>()
            smallList.add(it.resIdfirst)
            smallList.add(it.resIdSecond)
            smallList.add(it.resIdTherd)
            smallList.add(it.resIdFour)

            idis.add(smallList)
        }
        idis.forEachIndexed() { index, it ->
            if (check(it)) {
                credits.value = credits.value?.plus(15)
                Log.e("true", "Win ${it}, $index  ${credits.value}")

            }
        }

        val column1 = mutableListOf<Int>()
        val column2 = mutableListOf<Int>()
        val column3 = mutableListOf<Int>()
        val column4 = mutableListOf<Int>()

        idis.forEach {
            column1.add(it[0])
            column2.add(it[1])
            column3.add(it[2])
            column4.add(it[3])
        }

        if (check(column1)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win column1 ${credits.value}")
        }
        if (check(column2)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win column2 ${credits.value}")
        }
        if (check(column3)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win column3 ${credits.value}")
        }
        if (check(column4)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win column4 ${credits.value}")
        }

        val diagonal1 = mutableListOf<Int>(
            idis[0][0],
            idis[1][1],
            idis[2][2],
            idis[3][3],
        )

        val diagonal2 = mutableListOf<Int>(
            idis[0][3],
            idis[1][2],
            idis[2][1],
            idis[3][0],
        )

        if (check(diagonal1)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win diagonal1 ${credits.value}")
        }
        if (check(diagonal2)) {
            credits.value = credits.value?.plus(15)
            Log.e("true", "Win diagonal2 ${credits.value}")
        }
    }

    private fun check(sours: List<Int>): Boolean {
        return sours.groupingBy { it }.eachCount().filter { it.value == 3 }.isNotEmpty()
    }

    private fun addRandomSlot() {

        Log.e("Credits", "Credistsssss is ${credits.value}")
        credits.value = credits.value?.minus(20)
        Log.e("Credits", "Credistttt is ${credits.value}")
        val list = items.value
        for (i in 1..4) {
            list?.add(
                Item(
                    resIdfirst = resIdis.random(),
                    resIdSecond = resIdis.random(),
                    resIdTherd = resIdis.random(),
                    resIdFour = resIdis.random(),
                    id = 1
                )
            )
        }
        items.value = list
    }

    fun addMoney() {
        credits.value = credits.value?.plus(100)
    }

}