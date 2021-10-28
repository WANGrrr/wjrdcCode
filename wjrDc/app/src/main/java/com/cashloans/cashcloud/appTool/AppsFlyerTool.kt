package com.cashloans.cashcloud.appTool

import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.github.gzuliyujiang.oaid.DeviceID
import com.cashloans.thloans.appTool.App

object AppsFlyerTool {

     fun event(event: String){
         var map = HashMap<String,Any>()
         map.put("imei", DeviceID.getClientId())
         Log.d("imei", map.toString()+event)
         AppsFlyerLib.getInstance().trackEvent(App.getInstance(),event,map)
     }
}