package com.kqknuc.kdkuloq.http;

import com.google.gson.GsonBuilder;
import com.kqknuc.kdkuloq.app.Constant;
import com.kqknuc.kdkuloq.http.okhttp.CookieInterceptor;
import com.kqknuc.kdkuloq.http.okhttp.HttpCache;
import com.kqknuc.kdkuloq.http.okhttp.LogInterceptor;
import com.kqknuc.kdkuloq.http.okhttp.TrustManager;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Eren
 * <p>
 * 封装Retrofit
 */
public class RetrofitHttp {

    private static RetrofitHttp mRetrofitHttp;
    private static CookieInterceptor cookieInterceptor = new CookieInterceptor();
    private Retrofit retrofit;

    private RetrofitHttp() {
    }

    public static RetrofitHttp getInstance() {
        if (mRetrofitHttp == null) {
            synchronized (RetrofitHttp.class) {
                if (mRetrofitHttp == null) {
                    mRetrofitHttp = new RetrofitHttp();
                }
            }
        }
        return mRetrofitHttp;
    }

    /**
     * 调用接口
     *
     * @return 网络接口
     */
    public ApiService getApiService() {
        initRetrofit(Constant.URL_HOST);
        return retrofit.create(ApiService.class);
    }

    private void initRetrofit(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient builder = new OkHttpClient.Builder()
                    // SSL证书
                    .sslSocketFactory(TrustManager.getUnsafeOkHttpClient())
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    // Time out
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    // 增加头部信息
//                    .addInterceptor(headerInterceptor)
                    // 设置Cache拦截器
                    .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(cookieInterceptor)
                    .cache(HttpCache.getCache())
                    // 打印日志
                    .addInterceptor(new LogInterceptor())
                    // 失败重连
                    .retryOnConnectionFailure(true)
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    // 添加Gson转换器
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                    // 支持RxJava
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(builder)
                    .build();
        }
    }
}