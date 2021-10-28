package com.cashloans.cashcloud.models

import com.google.gson.annotations.SerializedName

data class yysResult(
    @SerializedName("code") var code: String ,
    @SerializedName("msg") var msg: String,
    @SerializedName("data") var data: List<renzhengResult>,
    @SerializedName("time") var time: String,

    )
