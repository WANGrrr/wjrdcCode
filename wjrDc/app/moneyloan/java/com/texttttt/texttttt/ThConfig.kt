package  com.Installment.Installment

class ThConfig() {
    companion object {
    // dev prod
    const val APP_RUN_ENVIRONMENT = "prod"
    var APP_NAME = "test"
    var APPSFLYER_KEY = "XT9wezxDgtU5KaxCmCcqzb"
    var ISGOOGLE = true
    const val baseUrl = "https://app.installment.vip"
    const val BASE_URL_PROD = "https://app.installment.vip"
    const val KEFU_JSON = "https://kefu-bucket.s3-accelerate.amazonaws.com/kfurl.json"
}
}