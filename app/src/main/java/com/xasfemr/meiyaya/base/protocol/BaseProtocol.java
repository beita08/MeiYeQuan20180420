package com.xasfemr.meiyaya.base.protocol;

import java.io.Serializable;

/**
 * 最外层Json实体
 * Created by lawson on 2017/7/3.
 */

public class BaseProtocol<T> implements Serializable {

    private static final String ERROR_CODE_025 = "025";
    private static final String ERROR_CODE_023 = "023";
    private static final String ERROR_CODE_030 = "030";

    public int status;
    public int code;
    public T data;
    public String message;


    public boolean isDataEmpty(){
        return data==null;
    }


    /**
     * 登录过期
     */
    /**
     * 令牌失效
     */
    public boolean isTokenExpired() {
        return ERROR_CODE_025.equals(code) || ERROR_CODE_023.equals(code);
    }
}
