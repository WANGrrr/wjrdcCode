package com.cashloans.cashcloud

import android.util.Log
import com.hjq.toast.ToastUtils
import com.cashloans.cashcloud.appTool.*
import com.cashloans.cashcloud.appTool.httpManger.BasePresenter
import com.cashloans.cashcloud.appTool.httpManger.DataManager
import com.cashloans.cashcloud.appTool.httpManger.RemoteDataObserver
import com.cashloans.cashcloud.models.S3UploadImgInfo
import com.cashloans.thloans.appTool.BaseBean

/**
 * Author: Want-Sleep
 * Date: 2019/07/15
 * Desc:
 */

class MainHomePresenter(view: MainActivesCon.View) : BasePresenter<MainActivesCon.View>(view),
    MainActivesCon.Presenter {
    companion object {
        const val s3UploadJsonAppIcon = 0x02
        const val s3UploadJsonAppImage = 0x04
    }
    override fun s3uploadFile(type :Int) {

        DataManager.INSTANCE.s3UploadImg("application/json").subscribe(object : RemoteDataObserver<S3UploadImgInfo>(mView) {
            override fun onNext(t: S3UploadImgInfo) {
                super.onNext(t)
                Log.d("S3UploadImgInfo", t.toString())
                mView?.UpLoadFileRes(t, type)
            }

            override fun onError(throwable: Throwable) {
                loadDialog.hideLoadingDialog()
                ToastUtils.show(throwable.message.toString())
                super.onError(throwable)
            }
        })
    }


    override fun uploadJson(jsonUrl: String) {
        var map = HashMap<String, @JvmSuppressWildcards Any>()
        map.put("jsonurl", jsonUrl)
        Log.d("jsonurl", map.toString())
        DataManager.INSTANCE.uploadAppList(map).subscribe(object : RemoteDataObserver<BaseBean>(mView) {

            override fun onNext(t: BaseBean) {
                super.onNext(t)
            }

            override fun onError(throwable: Throwable) {
                super.onError(throwable)
            }

        })
    }
    override fun uploadImageJson(jsonUrl: String, size: Int) {
        var map = HashMap<String, @JvmSuppressWildcards Any>()
        map.put("jsonurl", jsonUrl)
        map.put("image_count", size)
        Log.d("jsonurl", map.toString())
        DataManager.INSTANCE.uploadAppimage(map).subscribe(object : RemoteDataObserver<BaseBean>(mView) {

            override fun onNext(t: BaseBean) {
                super.onNext(t)
            }

            override fun onError(throwable: Throwable) {
                super.onError(throwable)
            }

        })
    }


}
