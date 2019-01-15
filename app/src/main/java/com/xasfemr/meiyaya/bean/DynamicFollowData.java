package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/16 0016 14:46
 */

public class DynamicFollowData {

    public int      status;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public String uid;
        public String userid;
        public String attention;
    }
}
