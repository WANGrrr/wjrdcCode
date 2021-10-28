package com.cashloans.cashcloud.models


data class ocrIdcard(
        var data:Data
)  //发卡人员（Displayed in Thai）
data class Data (val typeEN: String,   //身份证类型
                 val idNumber: String, //身份证号码
                 val nameEN: String,  //人名
                 val lastNameEN: String,  //姓
                 val birthdayEN: String, //生日
                 val issueDateEN: String, //发卡日期
                 val expiryDateEN: String, //有效期限
                 val serialNumber: String, //不同区域的编号
                 val typeTH: String,  //身份证类型（Displayed in Thai）
                 val religionTH: String,  //宗教（Displayed in Thai）
                 val nameTH: String,   // 泰文名（Displayed in Thai）
                 val birthdayTH: String,  //生日（Displayed in Thai）
                 val addressTH: String, //地址（Displayed in Thai）
                 val issueDateTH: String,  //发卡日期（Displayed in Thai）
                 val expiryDateTH: String, //有效期限（Displayed in Thai）
                 val issuingOfficerTH: String)