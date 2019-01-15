package com.xasfemr.meiyaya.http;





import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.xasfemr.meiyaya.http.exception.ErrorMessageFactory;
import com.xasfemr.meiyaya.http.exception.NetworkConnectionException;
import com.xasfemr.meiyaya.utils.LogUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;


/**
 * 描述：
 * Created by sen.luo on 2017/7/4.
 */

public abstract class SFSubscriber<E> extends Subscriber<E>{

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.show("访问异常",e.getMessage());

        if (handleResponseError((Exception) e)){
            return;
        }

        if(!TextUtils.isEmpty(e.getMessage())&&e.getMessage().contains("http")){
            onFailure("服务异常，请稍后再试...");
        }else {
            onFailure(e.getMessage());
        }

//        if (e instanceof HttpException){
//            HttpException httpException = (HttpException) e;
//            int code = httpException.code();
//            String msg = httpException.getMessage();
//            if (code == 504) {
//                msg = "网络不给力";
//            }
//            if (code == 502 || code == 404) {
//                msg = "服务器异常，请稍后再试";
//            }
//            onNetworkFailure(msg);
//        }else {
//            onFailure((Exception) e);
//        }
    }


    protected boolean handleResponseError (Exception e){
        String message = ErrorMessageFactory.create(e);

        if (e instanceof NetworkConnectionException || e instanceof UnknownHostException || e instanceof SocketTimeoutException){
            onNetworkFailure(message);
            return true;
        }

        if (e instanceof JsonSyntaxException) {
            onFailure("解析异常");
            return true;
        }

        if (e instanceof HttpException) {
            onNetworkFailure("服务异常，请稍后再试...");
            return true;
        }

        return false;
    }

    @Override
    public void onNext(E e) {
        onSuccess(e);
    }


    protected abstract void onSuccess(E result);
    protected abstract void onFailure(String msg);
    protected abstract void onNetworkFailure(String message);
}
