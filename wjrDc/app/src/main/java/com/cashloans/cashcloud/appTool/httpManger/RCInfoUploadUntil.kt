package com.cashloans.cashcloud.appTool.httpManger

import android.os.Looper
import android.util.Log
import com.hjq.toast.ToastUtils
import com.cashloans.cashcloud.appTool.AppsFlyerTool
import com.cashloans.cashcloud.appTool.loadDialog
import com.cashloans.cashcloud.models.S3UploadImgInfo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class RCInfoUploadUntil {


    private var mCallBack1 : CallBackInterface? = null

    fun setCallBack1(callBack: CallBackInterface) {
        this.mCallBack1 = callBack
    }

    interface CallBackInterface {
        fun fileUploadSuccess()
    }


    fun geturlStringFromS3(s3uploadImgInfo: S3UploadImgInfo, imgPath: String){

        Log.d("s3uploadImgInfo",s3uploadImgInfo.toString()+"imgPath:"+imgPath)
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

        //post方式提交的数据
        var file = File(imgPath)
        var imgPath = imgPath.split(".")

        var body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("key",s3uploadImgInfo.param.key)
                .addFormDataPart("Policy",s3uploadImgInfo.param.Policy)
                .addFormDataPart("x-amz-date",s3uploadImgInfo.param.x_amz_date)
                .addFormDataPart("x-amz-signature",s3uploadImgInfo.param.x_amz_signature)
                .addFormDataPart("x-amz-algorithm",s3uploadImgInfo.param.x_amz_algorithm)
                .addFormDataPart("x-amz-credential",s3uploadImgInfo.param.x_amz_credential)
                .addFormDataPart("Content-Type",s3uploadImgInfo.param.Content_Type)
                .addFormDataPart("file",file.name, RequestBody.create(("image/"+imgPath[imgPath.size-1]).toMediaTypeOrNull(),file)).build()

        val request: Request = Request.Builder()
                .url(s3uploadImgInfo.urlString) //请求的url
                .post(body)
                .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                var code = response.code
                if (200<code&&code<299) {
                    loadDialog.hideLoadingDialog()
                    AppsFlyerTool.event("identity_ocrphoto_upload_sucess")
                }else {
                    AppsFlyerTool.event("identity_ocrphoto_upload_fail")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                AppsFlyerTool.event("identity_ocrphoto_upload_fail")
                val mainHandler = android.os.Handler(Looper.getMainLooper())
                mainHandler.post {
                    ToastUtils.show("image upload error please try again")
                }
                loadDialog.hideLoadingDialog()
            }
        })
    }

    fun uploadFile(s3uploadImgInfo: S3UploadImgInfo, filePath: String){
        Log.d("s3uploadImgInfo",s3uploadImgInfo.toString())
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

        //post方式提交的数据
        var file = File(filePath)
        var imgPath = filePath.split(".")

        var body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("key",s3uploadImgInfo.param.key)
                .addFormDataPart("Policy",s3uploadImgInfo.param.Policy)
                .addFormDataPart("x-amz-date",s3uploadImgInfo.param.x_amz_date)
                .addFormDataPart("x-amz-signature",s3uploadImgInfo.param.x_amz_signature)
                .addFormDataPart("x-amz-algorithm",s3uploadImgInfo.param.x_amz_algorithm)
                .addFormDataPart("x-amz-credential",s3uploadImgInfo.param.x_amz_credential)
                .addFormDataPart("Content-Type",s3uploadImgInfo.param.Content_Type)
                .addFormDataPart("file",file.name, RequestBody.create(("application/"+imgPath[imgPath.size-1]).toMediaTypeOrNull(),file)).build()
        val request: Request = Request.Builder()
                .url(s3uploadImgInfo.urlString) //请求的url
                .post(body)
                .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                var code = response.code
                if (200<code && code<299) {
                    loadDialog.hideLoadingDialog()
                    mCallBack1?.fileUploadSuccess()
                    AppsFlyerTool.event("appinfolist_upload_sucess")
                }else {
                    AppsFlyerTool.event("appinfolist_upload_fail")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                AppsFlyerTool.event("appinfolist_upload_fail")
                val mainHandler = android.os.Handler(Looper.getMainLooper())
                mainHandler.post {
                    //使用java Gson的方式
                    ToastUtils.show("image upload error please try again")
                }
                loadDialog.hideLoadingDialog()
            }
        })
    }

}