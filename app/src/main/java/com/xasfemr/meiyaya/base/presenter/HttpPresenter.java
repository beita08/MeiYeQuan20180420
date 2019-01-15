package com.xasfemr.meiyaya.base.presenter;


import com.xasfemr.meiyaya.application.MyApplication;
import com.xasfemr.meiyaya.base.protocol.BaseProtocol;
import com.xasfemr.meiyaya.http.ApiService;
import com.xasfemr.meiyaya.http.SFHttpClient;
import com.xasfemr.meiyaya.http.exception.NetworkConnectionException;
import com.xasfemr.meiyaya.http.exception.TokenExpiredException;
import com.xasfemr.meiyaya.utils.NetUtils;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/7/5.
 */

public class HttpPresenter {

    protected ApiService api(){
        return SFHttpClient.api();
    }

    protected  <T>Observable<BaseProtocol<T>>SFHttp(Observable<BaseProtocol<T>> observable){

        if (!NetUtils.isConnected(MyApplication.getIns())){
            return Observable.error(new NetworkConnectionException());
        }

        Observable<BaseProtocol<T>> responseObservable = responseObservable(observable);
        return responseObservable.retryWhen(erros -> erros.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(Throwable error) {

                if (error instanceof NetworkConnectionException){

                    return Observable.error(new NetworkConnectionException());
                }

                return Observable.error(error);
            }
        }));


    }


    private <T> Observable<BaseProtocol<T>>responseObservable(Observable<BaseProtocol<T>> observable){

        return observable.flatMap(response -> {

            if (response==null){
                return Observable.error(new NetworkConnectionException());
            }else if (response.isTokenExpired()){

                return Observable.error(new TokenExpiredException());
            }
            return Observable.just(response);
        });
    }



}
