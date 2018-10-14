package com.thirtydegreesray.openhub.http.core;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.thirtydegreesray.openhub.AppApplication;
import com.thirtydegreesray.openhub.type.CustomType;
import com.thirtydegreesray.openhub.util.FileUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public enum AppApollo {
    INSTANCE;

    private final String TAG = "AppApollo";

    private ApolloClient apolloClient;
    private String token;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private CustomTypeAdapter<Date> dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {

        @Override
        public Date decode(CustomTypeValue value) {

            try {
                return DATE_FORMAT.parse(value.value.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public CustomTypeValue encode(Date value) {

            return new CustomTypeValue.GraphQLString(DATE_FORMAT.format(value));
        }
    };

    private CustomTypeAdapter<Uri> uriCustomTypeAdapter = new CustomTypeAdapter<Uri>() {

        @Override
        public Uri decode(@NotNull final CustomTypeValue value) {

            return Uri.parse(value.value.toString());
        }

        @NotNull
        @Override
        public CustomTypeValue encode(@NotNull final Uri value) {

            return null;
        }
    };

    private CustomTypeAdapter<String> htmlCustomTypeAdapter = new CustomTypeAdapter<String>() {

        @Override
        public String decode(@NotNull final CustomTypeValue value) {

            return value.value.toString();
        }

        @NotNull
        @Override
        public CustomTypeValue encode(@NotNull final String value) {

            return null;
        }
    };

    private CustomTypeAdapter<String> gitSshRemoteCustomTypeAdapter = new CustomTypeAdapter<String>() {

        @Override
        public String decode(@NotNull final CustomTypeValue value) {

            return value.value.toString();
        }

        @NotNull
        @Override
        public CustomTypeValue encode(@NotNull final String value) {

            return null;
        }
    };

    private void createApollo(@NonNull String baseUrl) {

        //Directory where cached responses will be stored
        File file = FileUtil.getCacheDir(AppApplication.get(), "/apollo-cache");

        //Size in bytes of the cache
        int size = 1024 * 1024;

        //Create the http response cache store
        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        //Build the Apollo Client
        apolloClient = ApolloClient.builder()
                .serverUrl(baseUrl)
                .addCustomTypeAdapter(CustomType.DATETIME, dateCustomTypeAdapter)
                .addCustomTypeAdapter(CustomType.URI, uriCustomTypeAdapter)
                .addCustomTypeAdapter(CustomType.HTML, htmlCustomTypeAdapter)
                .addCustomTypeAdapter(CustomType.GITSSHREMOTE, gitSshRemoteCustomTypeAdapter)
                .httpCache(new ApolloHttpCache(cacheStore))
                .okHttpClient(AppOkHttp.INSTANCE.getOkHttpClient(token))
                .build();
    }

    public synchronized ApolloClient getApolloClient(@NonNull String baseUrl, @Nullable String token) {

        this.token = token;
        if (apolloClient == null) {
            createApollo(baseUrl);
        }

        return apolloClient;
    }
}
