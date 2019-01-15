package com.xasfemr.meiyaya.bean;

import java.io.Serializable;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/20 0020 19:03
 */

public class LookUserData {

    public int      code;
    public int      status;
    public String   message;
    public UserInfo data;

    public static class UserInfo implements Serializable {

        public String id;
        public String username;
        public int    ustatus;
        public int    sex;
        public String images;
        public String bgimg;
        public String follow;
        public String fans;
        public int    growth;
        public String birthday;
        public String region;
        public String profession;
        public String signture;
        public int    is_approve;
        public String age;
        public int    attention;
    }
}
