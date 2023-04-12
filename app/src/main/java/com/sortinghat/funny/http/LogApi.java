package com.sortinghat.funny.http;

import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.common.utils.BuildFactory;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by wzy on 2021/8/6
 */
public interface LogApi {

    class Builder {
        public static LogApi getUnifyLogServer() {
            return BuildFactory.getInstance().create(LogApi.class, HttpUtils.funnyUnifyLogUrl);
        }
    }

    /**
     * 新的统计埋点，1.4.4版本
     */

    @POST("unifylog")
    Observable<Object> setAppUnifyLog(@Body RequestBody body);

}
