package com.cashloans.cashcloud.appTool.httpManger

import com.cashloans.cashcloud.appTool.SPHelper
import com.cashloans.cashcloud.models.S3UploadImgInfo
import com.cashloans.thloans.appTool.BaseBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc: 数据管理类（网络请求，数据库读写，File，SP）
 */
class DataManager private constructor() {

    companion object {
        val INSTANCE: DataManager by lazy {
            DataManager()
        }
    }

    // 存数据到SharedPreferences
    fun saveDataToSP(key: String, value: String) {
        SPHelper.getInstance().put(key, value)
    }

    // 从SharedPreferences取数据
    fun getFromSshredPerences(key: String): String {
        return SPHelper.getInstance().getString(key)
    }

    // 移除SharedPreferences中的数据
    fun removeInSP(key: String) {
        SPHelper.getInstance().remove(key)
    }

    //s3预上传
    fun s3UploadImg(imageType :String):Observable<S3UploadImgInfo> {
        return RetrofitHelper.getApiService().s3UploadImg(imageType).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
    //上传手机应用信息
    fun uploadAppList(appInfo: Map<String,@JvmSuppressWildcards Any>):Observable<BaseBean> {
        return RetrofitHelper.getApiService().uploadAppList(appInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }
    //上传手机图片信息
    fun uploadAppimage(appInfo: Map<String,@JvmSuppressWildcards Any>):Observable<BaseBean> {

        return RetrofitHelper.getApiService().uploadAppimage(appInfo).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    }




}
