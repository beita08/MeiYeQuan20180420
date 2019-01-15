package com.xasfemr.meiyaya.http.exception;

/**
 * 描述：网络连接异常
 * Created by sen.luo on 2017/7/5.
 */
public class NetworkConnectionException extends RuntimeException {

    public NetworkConnectionException() {

    }

    public NetworkConnectionException(String message) {
        super(message);
    }
}
