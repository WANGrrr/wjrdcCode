package com.cashloans.cashcloud.appTool.httpManger

import java.io.IOException

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:l
 */
class ApiException constructor(val errorCode: Int, val errorMsg: String) : IOException(errorMsg)
