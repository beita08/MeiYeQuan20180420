package com.xasfemr.meiyaya.http.exception;


import android.text.TextUtils;

import com.xasfemr.meiyaya.base.protocol.BaseProtocol;

/**
 * 描述：所有response非success的情况会抛出此异常
 * 在此根据code判断是什么类型的错误
 * Created by sen.luo on 2017/7/5.
 */
public class ResponseException extends RuntimeException {

    private BaseProtocol response;

    public <T> ResponseException(BaseProtocol<T> response) {
        this.response = response;
    }

    public BaseProtocol getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        if (response != null) {
            if(!TextUtils.isEmpty(response.message)){
                return response.message;
            }else {
                return "请求失败";
            }
        }
        return super.getMessage();
    }
}
