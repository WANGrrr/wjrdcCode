package com.cashloans.cashcloud.appTool.httpManger

/**
 * Author: Unknown
 * Date: 2019/05/29
 * Desc:
 */
interface IActivityLifecycle {
    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityCreated()

    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityStarted()

    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityResumed()

    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityPaused()

    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityStopped()

    /**
     * 在Application中通过反射调用
     *
     */
    fun onActivityDestroyed()
}
