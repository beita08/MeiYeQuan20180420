package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/17 0017 19:16
 */

public class LoginData {
    /**
     * code : 202
     * status : 202
     * message : 登录成功
     * data : {"id":"3","phonenumber":"17629006931","userpwd":"e10adc3949ba59abbe56e057f20f883e","datetime":"2017-12-14 16:14:37","status":"34","lstatus":"1","ustatus":"1","signture":"我我问问","invitation":"3","invite":"0","is_delete":"0","follow":"16","fans":"8","username":"听见下雨","images":"http://app.xasfemr.com/Public/Uploads/2017-11-30/5a1fa45661a8d.png","growth":"2190","sex":"1","birthday":"1991-01-01","region":"新疆维吾尔自治区-阿克苏地区-阿克苏市","bgimg":"http://app.xasfemr.com/Public/Uploads/2017-11-24/5a17e12fa1537.png","signdays":"55","money":"0","goldmoney":"235","zcmoney":"0","profession":"美业技师/学徒","edittime":"1513326524","grade":"6","ustatus_overtime":"1518170544","regid":null}
     */

    public int       code;
    public int       status;
    public String    message;
    public LoginInfo data;

    public static class LoginInfo {
        /**
         * id : 3
         * phonenumber : 17629006931
         * userpwd : e10adc3949ba59abbe56e057f20f883e
         * datetime : 2017-12-14 16:14:37
         * status : 34
         * lstatus : 1
         * ustatus : 1
         * signture : 我我问问
         * invitation : 3
         * invite : 0
         * is_delete : 0
         * follow : 16
         * fans : 8
         * username : 听见下雨
         * images : http://app.xasfemr.com/Public/Uploads/2017-11-30/5a1fa45661a8d.png
         * growth : 2190
         * sex : 1
         * birthday : 1991-01-01
         * region : 新疆维吾尔自治区-阿克苏地区-阿克苏市
         * bgimg : http://app.xasfemr.com/Public/Uploads/2017-11-24/5a17e12fa1537.png
         * signdays : 55
         * money : 0
         * goldmoney : 235
         * zcmoney : 0
         * profession : 美业技师/学徒
         * edittime : 1513326524
         * grade : 6
         * ustatus_overtime : 1518170544
         * regid : null
         */

        public String id;
        public String phonenumber;
        public String userpwd;
        public String datetime;
        public int    status;
        public int    lstatus; //lstatus：讲师状态（1为讲师）
        public int    ustatus;
        public String signture;
        public String invitation;
        public int    invite;
        public int    is_delete;
        public int    follow;
        public int    fans;
        public String username;
        public String is_approve;//是否完成企业认证  0未认证  1 认证
        public String images;
        public int    growth;
        public int    sex;
        public String birthday;
        public String region;
        public String bgimg;
        public int    signdays;
        public String money;
        public int    goldmoney;
        public String zcmoney;
        public String profession;
        public String edittime;
        public String grade;
        public String ustatus_overtime;
        public Object regid;

    }
}