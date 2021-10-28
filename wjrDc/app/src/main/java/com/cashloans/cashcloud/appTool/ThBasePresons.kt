package com.cashloans.cashcloud.appTool

import com.cashloans.cashcloud.appTool.httpManger.IActivityLifecycle
import com.cashloans.cashcloud.appTool.httpManger.IBaseView

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
interface ThBasePresons<T : IBaseView> : IActivityLifecycle {
    fun attachView(view: T)

    fun detachView()

    fun destroy()
}
