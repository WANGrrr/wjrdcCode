package com.cashloans.cashcloud.appTool.httpManger

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
interface IBasePresenter<T : IBaseView> : IActivityLifecycle {
    fun attachView(view: T)

    fun detachView()

    fun destroy()
}
