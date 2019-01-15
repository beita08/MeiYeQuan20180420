package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/13 0013 14:10
 */

public class CurrentMeUser {

    public int      status;
    public String   message;
    public UserInfo data;

    public static class UserInfo {

        public String id;
        public String phonenumber;
        public String userpwd;
        public String datetime;
        public int    status;
        public int    lstatus;
        public int    ustatus;
        public String signture;
        public String invitation;
        public int    invite;
        public int    is_delete;
        public String follow;
        public String fans;
        public String username;
        public String images;
        public int    growth;
        public int    sex;
        public String birthday;
        public String region;
        public String bgimg;
        public int    signdays;
        public String money;
        public int    goldmoney; //金币
        public String zcmoney;
        public String profession;
        public String edittime;
        public Object registration_id;
        public String grade;
        public String ustatus_overtime;
        public int    sign;
        public int rate; //兑换比例
        public String uname;// 实名姓名
    }
}
