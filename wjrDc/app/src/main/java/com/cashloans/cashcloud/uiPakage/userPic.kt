package com.cashloans.cashcloud.uiPakage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.hjq.toast.ToastUtils
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.nanchen.compresshelper.CompressHelper
import com.qpg.superhttp.SuperHttp
import com.qpg.superhttp.callback.SimpleCallBack
import com.cashloans.cashcloud.MainActivity
import com.cashloans.cashcloud.R
import com.cashloans.cashcloud.appTool.AppsFlyerTool
import com.cashloans.cashcloud.appTool.BaseActivity
import com.cashloans.cashcloud.appTool.httpManger.DataManager
import com.cashloans.cashcloud.appTool.httpManger.PhotoUtils
import com.cashloans.cashcloud.appTool.loadDialog
import com.cashloans.cashcloud.models.HttpNames
import com.cashloans.cashcloud.models.S3UploadImgInfo
import com.cashloans.thloans.appTool.BaseBean
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class userPic : BaseActivity() , View.OnClickListener{
    private lateinit var back_btn : RelativeLayout
    private lateinit var next_btn : Button
    private lateinit var idcard_img: ImageView
    private lateinit var clickImg :LinearLayout

    var idCardImgfile: String = ""
    var idcardUrl: String = ""
    var idcardStr:String = ""
    var ocrStr:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_user_pic
    }

    override fun initView() {
        AppsFlyerTool.event("identity_fivekeyspage_open")
        back_btn = findViewById(R.id.id_back)
        next_btn = findViewById(R.id.surebtns)
        clickImg = findViewById(R.id.idcard_stemp1)
        idcard_img = findViewById(R.id.realname_img)
        back_btn.setOnClickListener(this)
        next_btn.setOnClickListener(this)
        clickImg.setOnClickListener(this)



    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_back -> {
                val intents = Intent(this, MainActivity::class.java)
                startActivity(intents)
            }
            R.id.idcard_stemp1 -> {

                var strs = "@strings/app_page"+"fileprovider"
                ""
                EasyPhotos.createCamera(this, true)
                    .setFileProviderAuthority(strs)
                    .start(101) //也可以选择链式调用写法

            }
            R.id.surebtns -> {

                if (idcardStr != "ocrerror") {
                    Log.d("123123","dddddddddd")
                    val bundle = intent.extras
                    var liveId = bundle?.getString("livenessId")
                    val intent = Intent(this@userPic, idCardinfo::class.java)
                    intent.putExtra("realeName",idcardStr)
                    intent.putExtra("idCardUrl",idcardUrl)
                    intent.putExtra("livenessId",liveId)
                    startActivity(intent)
                    finish()
                }else{
                    Log.d("123123","qqqqqqqq")
                    ToastUtils.show(R.string.idcard_OCRError)

                }

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            //拍照并确定
            Log.d("PhotoUtils.PATH_PHOTO", PhotoUtils.PATH_PHOTO)

            AppsFlyerTool.event("identity_ocrphototake_sucess")

            var mSelected: List<Photo>? = ArrayList()

            mSelected =
                data?.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
            var results: Photo = mSelected?.get(0)!!

            val newFile = CompressHelper.getDefault(this).compressToFile(File(results.path))

            idcard_img.setImageURI(Uri.fromFile(newFile))
            idCardImgfile = newFile.toString()
            AppsFlyerTool.event("Success_Take_OCRPhoto")
            loadDialog.show(this, "loading...")
            sendImages(idCardImgfile)
            Log.d("234234234234","aaaaaaaaaaaaa")

            return
        }
    }

    fun sendImages(imagePath: String){


        SuperHttp.get(HttpNames.S3upLoads)
            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
            .request(object : SimpleCallBack<BaseBean>() {
                override fun onSuccess(data: BaseBean) {
                    var gson = Gson().fromJson(data.data, S3UploadImgInfo::class.java)
                    sendImgToS3(gson)

                }

                override fun onFail(errCode: Int, errMsg: String) {

                    Log.d("mesggg",errMsg)
                }
            })


    }
    fun sendImgToS3(s3model:S3UploadImgInfo){
        var file = File(idCardImgfile)
        var imgPath = idCardImgfile.split(".")

        var body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("key",s3model.param.key)
            .addFormDataPart("Policy",s3model.param.Policy)
            .addFormDataPart("x-amz-date",s3model.param.x_amz_date)
            .addFormDataPart("x-amz-signature",s3model.param.x_amz_signature)
            .addFormDataPart("x-amz-algorithm",s3model.param.x_amz_algorithm)
            .addFormDataPart("x-amz-credential",s3model.param.x_amz_credential)
            .addFormDataPart("Content-Type",s3model.param.Content_Type)
            .addFormDataPart("file",file.name, RequestBody.create(
                ("image/" + imgPath[imgPath.size - 1]).toMediaTypeOrNull(),
                file
            )).build()
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val request: Request = Request.Builder()
            .url(s3model.urlString) //请求的url
            .post(body)
            .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                var code = response.code
                if (200<code&&code<299) {
                    loadDialog.hideLoadingDialog()
                    AppsFlyerTool.event("identity_ocrphoto_upload_sucess")
                    idcardUrl = s3model.urlString + "/" + s3model.param.key
                    Log.d("imgss",idcardUrl)
                    ocrShbie(idcardUrl)
                }else {
                    AppsFlyerTool.event("identity_ocrphoto_upload_fail")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                AppsFlyerTool.event("identity_ocrphoto_upload_fail")
                val mainHandler = android.os.Handler(Looper.getMainLooper())
                mainHandler.post {
                    //使用java Gson的方式
                    ToastUtils.show("image upload error please try again")
                }
                loadDialog.hideLoadingDialog()
            }
        })
    }


    fun ocrShbie(urls : String){
        var map = HashMap<String,String>()
        map.put("imageData",urls)
        map.put("mimeType","image/jpeg")

        SuperHttp.post(HttpNames.getOcrResult)
            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences("token"))
            .setJson(Gson().toJson(map))
            .request(object : SimpleCallBack<BaseBean>() {
                override fun onSuccess(data: BaseBean) {

                    if (data.code == 0){
                        val bundle = intent.extras

                        idcardStr = data.data.toString()

                        var liveId = bundle?.getString("livenessId")
                        Log.d("qqqqqqqqqq",urls)
                        val intent = Intent(this@userPic, idCardinfo::class.java)
                        intent.putExtra("realeName",idcardStr)
                        intent.putExtra("idCardUrl",urls)
                        intent.putExtra("livenessId",liveId)
                        startActivity(intent)
                        finish()
                    }else{
                        ToastUtils.show(R.string.idcard_OCRError)

                        idcardStr = "ocrerror"
                    }
                }

                override fun onFail(errCode: Int, errMsg: String) {

                    Log.d("mesggg",errMsg)
                }
            })

    }

}