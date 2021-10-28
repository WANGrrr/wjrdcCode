package com.cashloans.cashcloud.appTool.httpManger;

import com.google.gson.JsonSyntaxException;
import com.hjq.toast.ToastUtils;
import com.cashloans.cashcloud.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Author: Unknown
 * Date: 2018/08/17
 * Desc:
 */
public class RemoteDataObserver<T> implements Observer<T> {

    private WeakReference<IBaseView> mBaseViewRef;

    protected RemoteDataObserver(IBaseView baseView) {
        mBaseViewRef = new WeakReference<>(baseView);
    }

    @Override
    public void onSubscribe(Disposable disposable) {}

    @Override
    public void onNext(T t) {}

    @Override
    public void onError(Throwable throwable) {
        handlerError(throwable);
    }

    @Override
    public void onComplete() {}

    private void handlerError(Throwable throwable) {
        IBaseView view = mBaseViewRef.get();
        if (view == null) {
            return;
        }
        if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            handleApiError(apiException);
        } else if (throwable instanceof SocketTimeoutException) {
            ToastUtils.show(R.string.error_timeout);
        } else if (throwable instanceof JsonSyntaxException) {
            ToastUtils.show(R.string.error_json_syntax);
        } else if (throwable instanceof IOException) {
            ToastUtils.show(R.string.error_network);
        } else if (throwable instanceof HttpException) {
            ToastUtils.show(((HttpException) throwable).message());
        } else {
            ToastUtils.show(R.string.error_unknown);
        }
    }

    private void handleApiError(ApiException apiException) {
        int errorCode = apiException.getErrorCode();
        String errorMsg = apiException.getErrorMsg();
        ToastUtils.show(errorMsg);
        switch (errorCode) {
            case 101:
                DataManager.Companion.getINSTANCE().removeInSP(Constants.PREF_TOKEN);
                break;
        }
    }
}
