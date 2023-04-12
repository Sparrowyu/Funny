package com.sortinghat.common.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sortinghat.common.BuildConfig;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.RequestInfo;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wzy on 2021/3/10
 * 网络请求工具类, 使用时请在"Application"下初始化
 */
public class HttpUtils {

    @SuppressLint("StaticFieldLeak")
    private volatile static HttpUtils instance;
    private Gson gson;
    private Context context;

    public final static String funnyBaseUrl = BuildConfig.FUNNY_BASE_URL;
    public final static String funnyTaskBaseUrl =  BuildConfig.FUNNY_BASE_TASK_URL;
    public final static String funnyUnifyLogUrl = BuildConfig.FUNNY_UNIFY_LOG_URL;
    public final static String webBase = "http://www.sortinghat.cn/";
    //测试环境
//    public final static String funnyBaseUrl = "http://39.107.224.111:8899/api/";
//    public final static String funnyTaskBaseUrl = "http://39.107.224.111:8855/task/";
//    public final static String funnyUnifyLogUrl = "http://39.107.224.111:8605/logkit/";
    //正式环境
//    public final static String funnyBaseUrl = "https://server.sortinghat.cn/api/";
//    public final static String funnyTaskBaseUrl = "https://server.sortinghat.cn/task/";
//    public final static String funnyUnifyLogUrl = "https://unifylog1.gaoxiaoxingqiu.com/logkit/";//正式目前两个域名，都可以用
//    public final static String funnyUnifyLogUrl = "https://unifylog2.gaoxiaoxingqiu.com/logkit/";

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public Retrofit.Builder getBuilder(String apiUrl) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(getOkClient());
        builder.baseUrl(apiUrl);// 设置远程地址
        builder.addConverterFactory(new NullOnEmptyConverterFactory());
        builder.addConverterFactory(GsonConverterFactory.create(getGson()));
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        return builder;
    }

    private Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setLenient();
            builder.setFieldNamingStrategy(new AnnotateNaming());
            builder.serializeNulls();
            gson = builder.create();
        }
        return gson;
    }

    private static class AnnotateNaming implements FieldNamingStrategy {
        @Override
        public String translateName(Field field) {
            ParamNames a = field.getAnnotation(ParamNames.class);
            return a != null ? a.value() : FieldNamingPolicy.IDENTITY.translateName(field);
        }
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        //Install the all-trusting trust manager TLS
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        //cache url
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        //50 MiB
        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                //Create an ssl socket factory with our all-trusting manager
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new HttpHeadInterceptor())
                //持久化cookie
                .addInterceptor(new ReceivedCookiesInterceptor(context))
                .addInterceptor(new AddCookiesInterceptor(context))
                //添加缓存，无网访问时会拿缓存,只会缓存get请求
                .addInterceptor(new AddCacheInterceptor(context))
//                .addInterceptor(new refreshTokenInterceptor())
                .cache(cache)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
        return okBuilder.build();
    }

    private OkHttpClient getOkClient() {
        return getUnsafeOkHttpClient();
    }

    private class HttpHeadInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                builder.addHeader("Accept", "application/json;versions=1");
                if (CheckNetwork.isNetworkConnected(context)) {
                    int maxAge = 60;
                    builder.addHeader("Cache-Control", "public, max-age=" + maxAge);
                } else {
                    int maxStale = 60 * 60 * 24 * 28;
                    builder.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
                }
                builder.addHeader("authToken", SPUtils.getInstance("user_info").getString("authToken"));
                builder.addHeader("appver", RequestInfo.getInstance(context).getVersionName() + "");
                builder.addHeader("os", "android");
                builder.addHeader("model", RequestInfo.getInstance(context).getModel());
                builder.addHeader("net", CheckNetwork.isWifiOr4G(context));
                builder.addHeader("channel", RequestInfo.getInstance(context).getUmChannel());
                String oaid = SPUtils.getInstance("user_info").getString("umeng_oaid");//addheader可以传""，不能传null
                if (TextUtils.isEmpty(oaid)) {
                    if (CommonUtils.OAID == null || TextUtils.isEmpty(CommonUtils.OAID)) {
                        builder.addHeader("OAID", "00000000-0000-0000-0000-000000000000");
                    } else {
                        builder.addHeader("OAID", CommonUtils.OAID);
                    }
                } else {
                    builder.addHeader("OAID", oaid);
                }
                builder.addHeader("isEmulator", RequestInfo.getInstance(context).isEmulator());
//            builder.addHeader("MAC", RequestInfo.getInstance(context).getMacAddressMd5());
                builder.addHeader("deviceId", DeviceUtils.getUniqueDeviceId());
                String user_agent = CommonUtils.getUserAgent();
                builder.addHeader("userAgent", user_agent);
                return chain.proceed(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    //获取token，可以自己觉得是否开启
    public class refreshTokenInterceptor implements Interceptor {

        private final String TAG = "okhttp---";

        @Override
        public Response intercept(Chain chain) throws IOException {
            Charset UTF8 = Charset.forName("UTF-8");

            // 打印请求报文
//            Request request = chain.request();
//            RequestBody requestBody = request.body();
//            String reqBody = null;
//            if(requestBody != null) {
//                Buffer buffer = new Buffer();
//                requestBody.writeTo(buffer);
//
//                Charset charset = UTF8;
//                MediaType contentType = requestBody.contentType();
//                if (contentType != null) {
//                    charset = contentType.charset(UTF8);
//                }
//                reqBody = buffer.readString(charset);
//            }
//            Log.d(TAG, String.format("发送请求\nmethod：%s\nurl：%s\nheaders: %s\nbody：%s",
//                    request.method(), request.url(), request.headers(), reqBody));

            // 打印返回报文
            // 先执行请求，才能够获取报文
            Response response = chain.proceed(chain.request());
            ResponseBody responseBody = response.body();
            String respBody = null;
            if (responseBody != null) {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        e.printStackTrace();
                    }
                }
                respBody = buffer.clone().readString(charset);
            }
            try {
                JSONObject jsonObject = new JSONObject(respBody);
                //处理返回数据.  // 406 临时TOKEN过期,或类型不对,此时用长期token刷新    407 长期TOKEN过期,请重新登录，
                int code = jsonObject.optInt("code", -1);
                if (code != -1) {
                    if (code == 405) {
                        CommonUtils.showLong("登录信息错误，请重新登录");
                    } else if (code == 406) {
                        JsonObject jsonObjectQuest = new JsonObject();
                        jsonObjectQuest.addProperty("nickName", "");
                        jsonObjectQuest.addProperty("userId", SPUtils.getInstance("user_info").getLong("user_id"));
                        jsonObjectQuest.addProperty("longTermToken", SPUtils.getInstance("user_info").getString("longTermToken"));

                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObjectQuest.toString());
                        Request newRequest = new Request.Builder()
                                .url(funnyBaseUrl + "refreshToken").post(body).build();
                        response = chain.proceed(newRequest);
                        //添加打印服务器返回的数据
                        ResponseBody responseBodynew = response.body();
                        long contentLength = responseBodynew.contentLength();
                        BufferedSource source = responseBodynew.source();
                        source.request(Integer.MAX_VALUE); // Buffer the entire body.
                        Buffer buffer = source.buffer();

                        if (contentLength != 0) {
                            String dataString = "" + buffer.clone().readString(Charset.forName("UTF-8"));
                            JSONObject json = new JSONObject(dataString);
                            //处理返回数据.  // 406 临时TOKEN过期,或类型不对,此时用长期token刷新    407 长期TOKEN过期,请重新登录，
                            int code1 = json.optInt("code", -1);

                            if (code1 == 0) {
                                String token = json.getJSONObject("data").optString("msg");
                                int days = json.getJSONObject("data").optInt("days", 30);

                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());

                                SPUtils.getInstance("user_info").put("authToken", token);
                                SPUtils.getInstance("user_info").put("token_days", days);
                                SPUtils.getInstance("user_info").put("token_date", date);

                            } else if (code1 == 405) {
                                CommonUtils.showLong(json.optString("msg"));
                            } else {
                                CommonUtils.showLong("请重新登录");
                            }
                            Log.e(TAG + "2", dataString);
                        }

                    } else if (code == 407) {

                        //调用那个游客登录接口
                        SPUtils.getInstance("user_info").put("authToken", "");
                        SPUtils.getInstance("user_info").put("longTermToken", "");
                        SPUtils.getInstance("user_info").put("user_id", -1L);
                        SPUtils.getInstance("user_info").put("user_status", 0);
                        SPUtils.getInstance("user_info").put("user_bind_phone", 0);
                        CommonUtils.showLong("请重新登录");

                        JsonObject jsonObjectQuest = new JsonObject();
                        jsonObjectQuest.addProperty("loginType", 0);
                        jsonObjectQuest.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObjectQuest.toString());
                        Request newRequest = new Request.Builder()
                                .url(funnyBaseUrl + "login").post(body).build();
                        response = chain.proceed(newRequest);
                        //添加打印服务器返回的数据
                        ResponseBody responseBodynew = response.body();
                        long contentLength = responseBodynew.contentLength();
                        BufferedSource source = responseBodynew.source();
                        source.request(Integer.MAX_VALUE); // Buffer the entire body.
                        Buffer buffer = source.buffer();

                        if (contentLength != 0) {
                            String dataString = "" + buffer.clone().readString(Charset.forName("UTF-8"));
                            JSONObject json = new JSONObject(dataString);
                            //处理返回数据.  // 406 临时TOKEN过期,或类型不对,此时用长期token刷新    407 长期TOKEN过期,请重新登录，
                            int code1 = json.optInt("code", -1);

                            if (code1 == 0) {
                                String token = json.getJSONObject("data").optString("authToken");
                                String longTermToken = json.getJSONObject("data").optString("longTermToken");
                                int days = json.getJSONObject("data").optInt("days", 30);
                                int status = json.getJSONObject("data").getJSONObject("userBase").optInt("status", 0);
                                long user_id = json.getJSONObject("data").getJSONObject("userBase").optLong("id");

                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());

                                SPUtils.getInstance("user_info").put("user_id", user_id);
                                SPUtils.getInstance("user_info").put("user_status", status);
                                SPUtils.getInstance("user_info").put("longTermToken", longTermToken);
                                SPUtils.getInstance("user_info").put("authToken", token);
                                SPUtils.getInstance("user_info").put("token_days", days);
                                SPUtils.getInstance("user_info").put("token_date", date);

                            } else if (code1 == 405) {
                                CommonUtils.showLong(json.optString("msg"));
                            } else {
                                CommonUtils.showLong("请重新登录");
                            }
                            Log.e(TAG + "2", dataString);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            return response;
        }
    }


    private static class AddCacheInterceptor implements Interceptor {
        private Context context;
        AddCacheInterceptor(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();
            Request request = chain.request();
            if (!CheckNetwork.isNetworkConnected(context)) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (CheckNetwork.isNetworkConnected(context)) {
                // read from cache
                int maxAge = 0;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                // tolerate 4-weeks stale
                int maxStale = 60 * 60 * 24 * 28;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }

    private static class ReceivedCookiesInterceptor implements Interceptor {

        private Context context;

        ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            //这里获取请求返回的cookie
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                List<String> d = originalResponse.headers("Set-Cookie");
//                Log.e("jing", "------------得到的 cookies:" + d.toString());

                // 返回cookie
                if (!TextUtils.isEmpty(d.toString())) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorConfig = sharedPreferences.edit();
                    String oldCookie = sharedPreferences.getString("cookie", "");

                    HashMap<String, String> stringStringHashMap = new HashMap<>();

                    // 之前存过cookie
                    if (!TextUtils.isEmpty(oldCookie)) {
                        String[] substring = oldCookie.split(";");
                        for (String aSubstring : substring) {
                            if (aSubstring.contains("=")) {
                                String[] split = aSubstring.split("=");
                                stringStringHashMap.put(split[0], split[1]);
                            } else {
                                stringStringHashMap.put(aSubstring, "");
                            }
                        }
                    }
                    String join = StringUtils.join(d, ";");
                    String[] split = join.split(";");

                    // 存到Map里
                    for (String aSplit : split) {
                        String[] split1 = aSplit.split("=");
                        if (split1.length == 2) {
                            stringStringHashMap.put(split1[0], split1[1]);
                        } else {
                            stringStringHashMap.put(split1[0], "");
                        }
                    }

                    // 取出来
                    StringBuilder stringBuilder = new StringBuilder();
                    if (stringStringHashMap.size() > 0) {
                        for (String key : stringStringHashMap.keySet()) {
                            stringBuilder.append(key);
                            String value = stringStringHashMap.get(key);
                            if (!TextUtils.isEmpty(value)) {
                                stringBuilder.append("=");
                                stringBuilder.append(value);
                            }
                            stringBuilder.append(";");
                        }
                    }

                    editorConfig.putString("cookie", stringBuilder.toString());
                    editorConfig.apply();
//                    Log.e("jing", "------------处理后的 cookies:" + stringBuilder.toString());
                }
            }
            return originalResponse;
        }
    }

    private static class AddCookiesInterceptor implements Interceptor {

        private Context context;

        AddCookiesInterceptor(Context context) {
            super();
            this.context = context;
        }

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String cookie = sharedPreferences.getString("cookie", "");
            builder.addHeader("Cookie", cookie);
            return chain.proceed(builder.build());
        }
    }

    final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};
}