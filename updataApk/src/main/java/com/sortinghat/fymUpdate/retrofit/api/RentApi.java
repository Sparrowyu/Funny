package com.sortinghat.fymUpdate.retrofit.api;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.RequestInfo;
import com.sortinghat.fymUpdate.common.ICallBack;
import com.sortinghat.fymUpdate.retrofit.Constant;
import com.sortinghat.fymUpdate.retrofit.api.interceptor.DownloadListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;

public class RentApi {

    private static final int TIME_OUT = 10;
    private static RentApi instance;
    public RentApiService service;
    private static OkHttpClient mOkHttpClient;

    private RentApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())//直接返回字符串，用上面的Gson不行
                .addConverterFactory(GsonConverterFactory.create())//Gson解析器
                .client(okHttpClient)
                .build();
        service = retrofit.create(RentApiService.class);
    }

    public static RentApi getInstance(Context mContext) {
        if (instance == null) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .addInterceptor(new HttpHeadInterceptor(mContext))
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .build();
            instance = new RentApi(mOkHttpClient);
        }
        return instance;
    }


    private static class HttpHeadInterceptor implements Interceptor {
        private Context context;

        public HttpHeadInterceptor(Context mContext) {
            this.context = mContext;
        }

        @NonNull
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
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

    public static void get(Context mContext, final String cUrl, final ICallBack success) {
        get(mContext, cUrl, success, null);
    }

    public static void get(Context mContext, final String cUrl, final ICallBack success, final ICallBack error) {
        Call<String> call = RentApi.getInstance(mContext).service.get(cUrl);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (success != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        success.result(response.body());
                    } else {
                        onFailure(call, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (error != null) {
                    error.result("failure");
                }
            }
        });
    }

    /**
     * 自定义域名的post方法
     */
    public static void post(Context mContext, final String cUrl, final String method, Map<String, RequestBody> params, final ICallBack success, final ICallBack error) {
        Call<String> call = RentApi.getInstance(mContext).service.post(cUrl + method, params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (success != null) {
                        String body = response.body();
                        if (response.isSuccessful() && body != null) {
                            success.result(body);
                        } else {
                            onFailure(call, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                try {
                    if (error != null) {
                        error.result("failure");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 自定义域名的post方法
     */
    public static void post(Context mContext, final String cUrl, @Body RequestBody body, final ICallBack success, final ICallBack error) {
        Call<String> call = RentApi.getInstance(mContext).service.post(cUrl, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (success != null) {
                        String body = response.body();
                        if (response.isSuccessful() && body != null) {
                            success.result(body);
                        } else {
                            onFailure(call, null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                try {
                    if (error != null) {
                        error.result("failure");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static void download(Context mContext, String url, final String path, final DownloadListener downloadListener) {
        Call<ResponseBody> call = RentApi.getInstance(mContext).service.down(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                writeResponseToDisk(path, response, downloadListener);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private static void writeResponseToDisk(final String path, final Response<ResponseBody> response, final DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //将输入流写入文件
    private static void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        //开始下载
        Log.e("download---", "onStartapi");
        downloadListener.onStart();
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onFail("createNewFile IOException");
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[1024 * 4];
            int len;
            while ((len = is.read(data)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
