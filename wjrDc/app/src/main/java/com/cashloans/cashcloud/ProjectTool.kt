package com.timelofirst.timelyloan.tool

import com.appsflyer.AppsFlyerLib
import com.cashloans.thloans.appTool.App
import com.github.gzuliyujiang.oaid.DeviceIdentifier

class ProjectTool {
    companion object {
        fun getoaid():String {
            // 获取OAID/AAID，同步调用
            var oaid = DeviceIdentifier.getOAID(App.getContext())
            if (checkoaid(oaid)) {
                oaid = DeviceIdentifier.getOAID(App.getContext())
            }
            if (checkoaid(oaid)) {
                oaid = AppsFlyerLib.getInstance().getAppsFlyerUID(App.getContext())
            }
            if (checkoaid(oaid)) {
                oaid = DeviceIdentifier.getGUID(App.getContext())
            }
            return oaid
        }
        fun checkoaid(oaid:String):Boolean {
            if (oaid.isNullOrEmpty() || oaid.equals("") || oaid.equals("null")){
                return false
            }
            return true
        }

    }

}