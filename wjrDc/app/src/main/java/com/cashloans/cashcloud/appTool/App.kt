package com.cashloans.thloans.appTool


import ai.advance.liveness.lib.GuardianLivenessDetectionSDK
import ai.advance.liveness.lib.Market
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.github.gzuliyujiang.oaid.DeviceID
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.qpg.superhttp.SuperHttp
import com.qpg.superhttp.cookie.CookieJarImpl
import com.qpg.superhttp.cookie.store.SPCookieStore
import com.qpg.superhttp.interceptor.HeadersInterceptorDynamic
import com.qpg.superhttp.interceptor.HttpLogInterceptor
import com.cashloans.cashcloud.appTool.ThLoanLocationUtils
import com.cashloans.cashcloud.appTool.httpManger.CrashHandler
import com.cashloans.cashcloud.appTool.httpManger.DataManager
import com.cashloans.cashcloud.appTool.httpManger.IActivityLifecycle
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.timelofirst.timelyloan.tool.ProjectTool


/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
class App : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private lateinit var INSTANCE : App
        private lateinit var context:Context
        fun getInstance(): App {
            return INSTANCE
        }
        fun getContext(): Context {
            return context
        }
    }



    private val mActivityStack: MutableList<Activity> = mutableListOf()

    override fun onCreate() {
        DeviceID.register(this)
        super.onCreate()
        INSTANCE = this

        DeviceIdentifier.register(this)
        context = this.applicationContext
        registerActivityLifecycleCallbacks(this)
        initAFSdk()
        initLivenessSDK()

        // 初始化吐司工具类
        ToastUtils.init(this);
        XXPermissions.setScopedStorage(true);

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance(this));

        //动态头部拦截器，可以用于添加token类似这种动态的头部,当然，也可以放普通请求头数据
        val headersInterceptor = HeadersInterceptorDynamic {
            val headers: HashMap<String, String> = HashMap()
            headers["Token"] = DataManager.INSTANCE.getFromSshredPerences("token")
            headers
        }

        val headers: MutableMap<String, String> = HashMap()
        headers["version"] = "2.0"
        headers["systemType"] = "android"

        val httpLogInterceptor = HttpLogInterceptor("-------->")
        httpLogInterceptor.level = HttpLogInterceptor.Level.BODY
        SuperHttp.init(this)

        val headerMap: HashMap<String, String> = HashMap()
        headerMap.put("Content-type","application/json")
        headerMap["Token"] = ""
        headerMap["AppName"] = ThConfig.APP_NAME
        headerMap["DeviceType"] = Build.MODEL
        headerMap["IMEI"] = ProjectTool.getoaid()
        headerMap["Token"] = DataManager.INSTANCE.getFromSshredPerences("token")


        val location = ThLoanLocationUtils.INSTANCE.getLocation()
        if (location != null) {
            headerMap["LAT"] = location.latitude.toString()
            headerMap["LNG"] = location.longitude.toString()

        }
        SuperHttp.config() //配置请求主机地址
            .setBaseUrl(ThConfig.baseUrl) //配置全局请求头
            .globalHeaders(headerMap) //配置全局请求参数 //配置读取超时时间，单位秒
            .setReadTimeout(30) //配置写入超时时间，单位秒
            .setWriteTimeout(30) //配置连接超时时间，单位秒
            .setConnectTimeout(30) //配置请求失败重试次数
            .setRetryCount(3) //配置请求失败重试间隔时间，单位毫秒
            .setRetryDelayMillis(1000) //配置是否使用cookie
            .isUseCookie(true) //配置自定义cookie
            .setCookie(CookieJarImpl(SPCookieStore(this)))
        //配置应用级拦截器
        .addInterceptor(httpLogInterceptor)
        //配置网络拦截器
//            .networkInterceptor(NoCacheInterceptor())
    }
    private fun initLivenessSDK() {
        GuardianLivenessDetectionSDK.init(this, Market.Thailand)
    }

    private fun initAFSdk() {
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
            }

            override fun onConversionDataFail(error: String?) {
                Log.e("LOG_TAG", "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {

            }
            override fun onAttributionFailure(error: String?) {
                Log.e("LOG_TAG", "error onAttributionFailure :  $error")
            }
        }
        AppsFlyerLib.getInstance().init(ThConfig.APPSFLYER_KEY, conversionDataListener, applicationContext)
        AppsFlyerLib.getInstance().startTracking(this)

    }




    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        mActivityStack.add(activity)
        activityLifecycleInjectIntoField(activity, "onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        activityLifecycleInjectIntoField(activity, "onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {
        activityLifecycleInjectIntoField(activity, "onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        activityLifecycleInjectIntoField(activity, "onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity) {
        activityLifecycleInjectIntoField(activity, "onActivityStopped")
//        loadingTool.hideLoadingDialog()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityStack.remove(activity)
        activityLifecycleInjectIntoField(activity, "onActivityDestroyed")
    }


    fun topActivity(): Activity {
        return mActivityStack[mActivityStack.size - 1]
    }

    private fun activityLifecycleInjectIntoField(activity: Activity, methodName: String) {
        try {
            val fields = activity.javaClass.declaredFields
            for (field in fields) {
                if (IActivityLifecycle::class.java.isAssignableFrom(field.type)) {
                    field.isAccessible = true
                    val method = IActivityLifecycle::class.java.getDeclaredMethod(methodName)
                    val instance = field.get(activity) as IActivityLifecycle?
                    method.invoke(instance)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
