package com.app.wildtreasure.ui

import android.content.Context
import com.onesignal.OneSignal

class OneWrapper(context: Context, uid: String) {
    init {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(Const.ONE_SIGNAL_ID)
        OneSignal.setExternalUserId(uid)
    }

    fun send(campaign: String, deep: String) {
        when {
            campaign == "null" && deep == "null" -> {
                OneSignal.sendTag("key2", "organic")
            }
            deep != "null" -> {
                OneSignal.sendTag("key2", deep.replace("myapp://", "").substringBefore("/"))
            }
            campaign != "null" -> {
                OneSignal.sendTag("key2", campaign.substringBefore("_"))
            }
        }
    }
}