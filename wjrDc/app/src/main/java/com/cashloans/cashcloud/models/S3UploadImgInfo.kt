package com.cashloans.cashcloud.models

import com.google.gson.annotations.SerializedName

data class S3UploadImgInfo (
    var param: S3Param,
    @SerializedName("url") val urlString:String


)
data class S3Param (
                  val key:String,
                  val Policy:String,
                  @SerializedName("x-amz-date") val x_amz_date:String,
                  @SerializedName("x-amz-signature") val x_amz_signature:String,
                  @SerializedName("x-amz-algorithm") val x_amz_algorithm:String,
                  @SerializedName("x-amz-credential") val x_amz_credential:String,
                  @SerializedName("Content-Type") val Content_Type:String)