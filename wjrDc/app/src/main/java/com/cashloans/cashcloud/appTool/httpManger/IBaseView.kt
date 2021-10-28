package com.cashloans.cashcloud.appTool.httpManger

import android.content.Context

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
interface IBaseView {
    // 有用到这个的注意内存泄漏！！！
    fun getContext(): Context
}
