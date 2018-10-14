package com.thirtydegreesray.openhub.http.core;

import android.app.AlertDialog;
import android.support.annotation.NonNull;

public class HttpProgressApolloSubscriber<T> extends HttpApolloSubscriber<T> {

    /**
     * 网络请求dialog
     */
    private AlertDialog mDialog;

    public HttpProgressApolloSubscriber(@NonNull AlertDialog dialog, @NonNull HttpObserver<T> observer) {

        super(observer);
        mDialog = dialog;
    }

    @Override
    public void onStart() {

        super.onStart();
        if (!isUnsubscribed()) {
            mDialog.show();
        }
    }

    @Override
    public void onCompleted() {

        super.onCompleted();
        mDialog.dismiss();
    }

    @Override
    public void onError(Throwable e) {

        super.onError(e);
        mDialog.dismiss();
    }
}
