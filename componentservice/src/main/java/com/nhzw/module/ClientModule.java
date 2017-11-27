package com.nhzw.module;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nhzw.service.AppService;
import com.xinayida.lib.utils.AppUtils;

import java.io.File;
import java.security.cert.CertificateException;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ww on 2017/9/15.
 */

@Module
public class ClientModule {
    private static final String CACHE_DIR = "network_cache";

    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if(AppService.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return logging;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Date.class, new DateDeserializer(DATE_PATTERN1, DATE_PATTERN2))
                .serializeNulls()
                .create();
        return gson;
    }

    @Provides
    @Singleton
    OkHttpClient provideCacheOkHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return //new OkHttpClient.Builder()
                getUnsafeOkHttpClient()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder builder = request.newBuilder();
                    int versionCode = AppUtils.getVersionCode(AppService.instance.getAppContext());
                    builder.addHeader("deviceId", AppUtils.getDeviceID(AppService.instance.getAppContext()))
                            .addHeader("os", "android")
                            .addHeader("version", String.valueOf(versionCode));
                    if (AppService.instance.getToken() != null) {
                        builder.addHeader("token", AppService.instance.getToken());
                        builder.addHeader("uid", String.valueOf(AppService.instance.getUid()));
                    }
                    Request newReq = builder.build();
                    if (AppService.DEBUG) {
                        Log.d("OkHttp", "header: " + newReq.headers().toMultimap().toString());
                    }
                    return chain.proceed(newReq);
                })
                .build();
    }

    private OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

//            OkHttpClient okHttpClient = builder.build();
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Named("cache")
//    @Provides
//    @Singleton
//    Retrofit provideCacheRetrofit(Context context, HttpLoggingInterceptor loggingInterceptor, Gson gson) {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .cache(new Cache(new File(AppUtils.getCacheDir(context), CACHE_DIR), 10 * 1024 * 1024))
//                .build();
//        return new Retrofit.Builder()
//                .client(okHttpClient)
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AppService.DOMAIN_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RxCache provideRxCache(Context context) {
        RxCache.Builder builder = new RxCache.Builder();
        return builder.persistence(new File(AppUtils.getCacheDir(context)), new GsonSpeaker());
    }
}
