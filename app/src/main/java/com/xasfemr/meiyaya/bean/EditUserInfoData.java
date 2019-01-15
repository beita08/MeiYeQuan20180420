package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/22 0022 17:08
 */

public class EditUserInfoData {

    public int      status;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public String id;
        public String username;
        public int    sex;
        public String birthday;
        public String region;
        public String signture;
        public String profession;

    }
}
