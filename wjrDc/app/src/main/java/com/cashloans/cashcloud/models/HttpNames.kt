package com.cashloans.cashcloud.models

class HttpNames {
    companion object {
//        获取实名认证的
        var getRealnametaskid = "/api/advance/liveness"
        var S3upLoads = "/api/upload/s3Upload?type=image/jpeg"
        var sendLiveIds = "/app/fengdun/face/auth"
        var getOcrResult = "/api/advance/ocr"
        var upLoadUserMessage= "/api/advance/realname"

        var UploadLianxiren = "/api/user/record/contact"
        var getyys = "/api/app/auth/config"
        var mineYys = "/api/my/thailand/operator"
        var faceBookNonce = "/api/fengdun/facebook"
        var appsFlyId = "/api/appsflyer/registration_id/"


    }
}