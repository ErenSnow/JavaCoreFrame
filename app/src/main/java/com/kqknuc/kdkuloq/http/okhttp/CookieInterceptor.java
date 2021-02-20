package com.kqknuc.kdkuloq.http.okhttp;

import com.kqknuc.kdkuloq.app.MyApplication;
import com.kqknuc.kdkuloq.utils.SpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author Eren
 * <p>
 * Cache拦截器
 */
public class CookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String cookie : originalResponse.headers("Set-Cookie")) {
                sb.append(cookie).append("-");
            }
            SpUtils.setParam(MyApplication.getInstance(), "cookies", sb.length() > 0 ? sb.subSequence(0, sb.length() - 1).toString() : "");
        }
        return originalResponse;
    }
}
