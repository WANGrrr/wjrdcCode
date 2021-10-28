package com.cashloans.cashcloud.appTool.httpManger

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Author: Unknown
 * Date: 2018/08/14
 * Desc:
 */
abstract class BasePresenter<T : IBaseView>(view: T) : IBasePresenter<T> {

    protected var mView: T? = null
    private val mDisposables: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    init {
        attachView(view)
    }

    override fun onActivityCreated() {}

    override fun onActivityStarted() {}

    override fun onActivityResumed() {}

    override fun onActivityPaused() {}

    override fun onActivityStopped() {}

    override fun onActivityDestroyed() {
        destroy()
    }

    final override fun attachView(view: T) {
        this.mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun destroy() {
        mDisposables.dispose()
        detachView()
    }

    protected fun addDisposable(disposable: Disposable) {
        mDisposables.add(disposable)
    }

}
