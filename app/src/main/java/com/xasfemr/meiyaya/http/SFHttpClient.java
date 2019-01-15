package com.xasfemr.meiyaya.http;



import com.xasfemr.meiyaya.BuildConfig;
import com.xasfemr.meiyaya.global.GlobalConstants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 描述：
 * Created by sen.luo on 2017/7/3.
 */

public class SFHttpClient {

    public static Retrofit mRetrofit;
    private static ApiService mApiservice;

    public static ApiService api(){

        return null==mApiservice ?mApiservice= retrofit().create(ApiService.class) : mApiservice;
    }


    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            if (BuildConfig.MieYeQuan_TEST) {
                // Log信息拦截器
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //设置 Debug Log 模式
                builder.addInterceptor(loggingInterceptor);
            }
            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(GlobalConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return mRetrofit;
    }

}
