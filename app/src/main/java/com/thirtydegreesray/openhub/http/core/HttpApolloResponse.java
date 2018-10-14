package com.thirtydegreesray.openhub.http.core;

import android.support.annotation.Nullable;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;

import java.util.List;

public class HttpApolloResponse<T extends Object> extends HttpResponse<T> {

    private Response<T> oriResponse;

    public HttpApolloResponse(Response<T> response) {

        super(null);

        oriResponse = response;
    }

    @Override
    public boolean isSuccessful() {

        return !oriResponse.hasErrors();
    }

    @Override
    public boolean isFromCache() {

        return oriResponse.fromCache();
    }

    @Override
    public boolean isFromNetWork() {

        return !oriResponse.fromCache();
    }

    private boolean isResponseEnable(@Nullable okhttp3.Response response) {

        return response != null && response.code() == 200;
    }

    @Override
    public retrofit2.Response<T> getOriResponse() {

        return null;
    }

    public T body() {

        return oriResponse.data();
    }

    public List<Error> getErrors() {

        return oriResponse.errors();
    }
}