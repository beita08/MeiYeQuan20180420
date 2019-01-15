package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/23 0023 16:09
 */

public class WechatPrepayInfo {

    public int        code;
    public String     message;
    public PrepayInfo data;

    public static class PrepayInfo {

        public String appid;
        public String noncestr;
        public String partnerid;
        public String prepayid;
        public int    timestamp;
        public String sign;
        public String packages;

    }
}
