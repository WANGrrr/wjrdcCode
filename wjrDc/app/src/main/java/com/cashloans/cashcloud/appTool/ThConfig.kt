package com.cashloans.thloans.appTool

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc: 配置数据
 */
class ThConfig {
    companion object {
        // dev prod
        const val APP_RUN_ENVIRONMENT = "prod"
        var APP_NAME = "test"
        var APPSFLYER_KEY = "6zrLqXaAFDbTpUM9p93HjL"
        var ISGOOGLE = true
        const val baseUrl = "https://app.cashcloudplus.vip"
        const val BASE_URL_PROD = "https://app.cashcloudplus.vip"
        const val KEFU_JSON = "https://kefu-bucket.s3-accelerate.amazonaws.com/kfurl.json"
    }
}
