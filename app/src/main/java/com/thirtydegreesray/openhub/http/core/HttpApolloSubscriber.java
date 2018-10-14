package com.thirtydegreesray.openhub.http.core;

import com.apollographql.apollo.api.Response;

import rx.Subscriber;

public class HttpApolloSubscriber<T> extends Subscriber<Response<T>> {

    private HttpObserver<T> mObserver;

    public HttpApolloSubscriber() {
    }

    public HttpApolloSubscriber(HttpObserver<T> observer) {
        mObserver = observer;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (mObserver != null)
            mObserver.onError(e);
    }

    @Override
    public void onNext(Response<T> r) {
        if (mObserver != null)
            mObserver.onSuccess(new HttpApolloResponse<>(r));
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
