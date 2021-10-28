package com.cashloans.cashcloud

import com.cashloans.cashcloud.appTool.httpManger.IBaseView
import com.cashloans.cashcloud.appTool.ThBasePresons
import com.cashloans.cashcloud.models.S3UploadImgInfo


/**
 * Author: Want-Sleep
 * Date: 2019/07/15
 * Desc:
 */

interface MainActivesCon {
    companion object {
        const val s3UploadJsonAppIcon = 0x02
        const val s3UploadJsonAppImage = 0x04
    }
    interface View : IBaseView {
        fun UpLoadFileRes(s3UploadImgInfo: S3UploadImgInfo, type: Int)
    }

    interface Presenter : ThBasePresons<View> {
        fun s3uploadFile(type:Int)
        fun uploadJson(jsonUrl: String)
        fun uploadImageJson(jsonUrl: String,size: Int)
    }
}
