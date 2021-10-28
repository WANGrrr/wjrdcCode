package com.cashloans.thloans.appTool

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc
 */
data class BaseBean(
        @SerializedName("code") var code: Int = 0,
        @SerializedName("msg") var message: String,
        @SerializedName("data") var data: JsonObject,
)
