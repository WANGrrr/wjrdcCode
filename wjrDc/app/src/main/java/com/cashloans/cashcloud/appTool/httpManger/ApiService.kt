package com.cashloans.cashcloud.appTool.httpManger
import com.cashloans.cashcloud.models.S3UploadImgInfo
import com.cashloans.thloans.appTool.BaseBean
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc: API
 */
interface ApiService {


    @GET ("/api/upload/s3Upload")
    fun s3UploadImg(@Query("type") imageType: String):Observable<S3UploadImgInfo>

    //上传手机应用信息
    @POST ("/api/user/record/app")
    fun uploadAppList(@Body param: Map<String,@JvmSuppressWildcards Any>) : Observable<BaseBean>

    //上传手机图片信息
    @POST ("/api/user/record/photo")
    fun uploadAppimage(@Body param: Map<String,@JvmSuppressWildcards Any>) : Observable<BaseBean>


}
