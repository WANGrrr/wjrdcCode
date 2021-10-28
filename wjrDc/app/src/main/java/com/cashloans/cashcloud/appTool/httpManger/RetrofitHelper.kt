package com.cashloans.cashcloud.appTool.httpManger

import android.os.Build
import android.util.Log
import com.github.gzuliyujiang.oaid.DeviceID
import com.cashloans.cashcloud.appTool.ThLoanLocationUtils
import com.cashloans.thloans.appTool.ThConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc: 网络请求管理类
 */
class RetrofitHelper private constructor() {

    companion object {
        val INSTANCE: RetrofitHelper by lazy {
            RetrofitHelper()
        }

        fun getApiService(): ApiService {
            return INSTANCE.mApiService
        }
    }

    private var mRetrofit: Retrofit
    private var mApiService: ApiService

    init {
        mRetrofit = Retrofit.Builder()
                .baseUrl(ConfigProvider.getBaseUrl())
                .client(buildOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ConverterFactory.create())
                .build()
        mApiService = mRetrofit.create(ApiService::class.java)
    }



    private fun buildOkHttpClient(): OkHttpClient {

        return OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor())
                .addInterceptor { chain ->
                    val original = chain.request()
                    var request = original.newBuilder()
                            .addHeader("Content-type", "application/json")
                            .addHeader("Token", DataManager.INSTANCE.getFromSshredPerences(Constants.PREF_TOKEN))
                            .addHeader("AppName", ThConfig.APP_NAME)
                            .addHeader("DeviceType", Build.MODEL)
                            .addHeader("IMEI", DeviceID.getClientId())
                            .build()
                    val location = ThLoanLocationUtils.INSTANCE.getLocation()
                    if (location != null) {
                                request = request.newBuilder()
                                        .addHeader("LAT", location.latitude.toString())
                                        .addHeader("LNG", location.longitude.toString())
                                        .build()
                            }
                    Log.d("network",request.toString())
                    chain.proceed(request)
                }
                .build()
    }
}
