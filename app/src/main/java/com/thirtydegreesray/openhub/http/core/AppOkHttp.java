package com.thirtydegreesray.openhub.http.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thirtydegreesray.openhub.AppApplication;
import com.thirtydegreesray.openhub.AppConfig;
import com.thirtydegreesray.openhub.AppData;
import com.thirtydegreesray.openhub.util.FileUtil;
import com.thirtydegreesray.openhub.util.NetHelper;
import com.thirtydegreesray.openhub.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum AppOkHttp {
    INSTANCE;

    private final String TAG = "AppOkHttp";
    private String token;

    private OkHttpClient createOkHttpClient() {

        int timeOut = AppConfig.HTTP_TIME_OUT;
        Cache cache = new Cache(FileUtil.getHttpImageCacheDir(AppApplication.get()),
                                AppConfig.HTTP_MAX_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(new BaseInterceptor())
                .addNetworkInterceptor(new NetworkBaseInterceptor())
                .cache(cache)
                .build();
    }

    public OkHttpClient getOkHttpClient(@Nullable String token) {

        this.token = token;
        return createOkHttpClient();
    }

    /**
     * 拦截器
     */
    private class BaseInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            Request request = chain.request();

            //add unique login id in url to differentiate caches
            if (AppData.INSTANCE.getLoggedUser() != null
                    && !AppConfig.isCommonPageUrl(request.url().toString())) {
                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter("uniqueLoginId",
                                           AppData.INSTANCE.getLoggedUser().getLogin())
                        .build();
                request = request.newBuilder()
                        .url(url)
                        .build();
            }

            //add access token
            if (!StringUtils.isBlank(token)) {
                String auth = token.startsWith("Basic") ? token : "token " + token;
                request = request.newBuilder()
                        .addHeader("Authorization", auth)
                        .build();
            }
            Log.d(TAG, request.url().toString());

            //第二次请求，强制使用网络请求
            String forceNetWork = request.header("forceNetWork");
            //有forceNetWork且无网络状态下取从缓存中取
            if (!StringUtils.isBlank(forceNetWork) &&
                    !NetHelper.INSTANCE.getNetEnabled()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            } else if ("true".equals(forceNetWork)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            }

            return chain.proceed(request);
        }
    }

    /**
     * 网络请求拦截器
     */
    private class NetworkBaseInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            Request request = chain.request();
            Response originalResponse = chain.proceed(request);

            //            String serverCacheControl = originalResponse.header("Cache-Control");
            String requestCacheControl = request.cacheControl().toString();

            //            //若服务器端有缓存策略，则无需修改
            //            if (StringUtil.isBlank(serverCacheControl)) {
            //                return originalResponse;
            //            }
            //            //不设置缓存策略
            //            else

            //有forceNetWork时，强制更改缓存策略
            String forceNetWork = request.header("forceNetWork");
            if (!StringUtils.isBlank(forceNetWork)) {
                requestCacheControl = getCacheString();
            }

            if (StringUtils.isBlank(requestCacheControl)) {
                return originalResponse;
            }
            //设置缓存策略
            else {
                Response res = originalResponse.newBuilder()
                        .header("Cache-Control", requestCacheControl)
                        //纠正服务器时间，服务器时间出错时可能会导致缓存处理出错
                        //                        .header("Date", getGMTTime())
                        .removeHeader("Pragma")
                        .build();
                return res;
            }

        }
    }

    public static String getCacheString() {

        return "public, max-age=" + AppConfig.CACHE_MAX_AGE;
    }
}
