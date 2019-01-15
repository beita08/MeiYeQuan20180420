package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/20 0020 10:02
 */

public class RegisterData {

    public int           status;
    public String        message;
    public RegisUserInfo data;

    public static class RegisUserInfo {

        public String id;
        public String phonenumber;
        public String userpwd;
        public String datetime;
        public String status;
        public String lstatus;
        public String ustatus;
        public String signture;
        public String invitation;
        public String invite;
        public String is_delete;
        public String follow;
        public String fans;
        public String username;
        public String images;
        public String growth;
        public String sex;
        public String birthdy;
        public String region;
        public String bgimg;
        public String signdays;
    }
}
