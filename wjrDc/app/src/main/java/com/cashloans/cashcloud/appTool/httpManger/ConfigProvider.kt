package com.cashloans.cashcloud.appTool.httpManger

import com.cashloans.thloans.appTool.ThConfig

/**
 * Author: Unknown
 * Date: 2018/10/19
 * Desc:
 */
class ConfigProvider {

    companion object {
        fun getBaseUrl(): String {
            return if (ThConfig.APP_RUN_ENVIRONMENT.equals("prod")) ThConfig.BASE_URL_PROD else ThConfig.baseUrl
        }
    }
}
