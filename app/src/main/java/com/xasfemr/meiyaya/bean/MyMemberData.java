package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/12/14 0014 11:08
 */

public class MyMemberData {

    /**
     * status : 200
     * message : 该用户是会员
     * data : {"id":"3","username":"听见下雨的声音","images":"http://app.xasfemr.com/Public/Uploads/2017-11-30/5a1fa45661a8d.png","ustatus":"1","ustatus_overtime":"2018年02月09日"}
     */

    public int        status;
    public String     message;
    public MemberInfo data;

    public static class MemberInfo {
        /**
         * id : 3
         * username : 听见下雨的声音
         * images : http://app.xasfemr.com/Public/Uploads/2017-11-30/5a1fa45661a8d.png
         * ustatus : 1
         * ustatus_overtime : 2018年02月09日
         */

        public String id;
        public String username;
        public String images;
        public String ustatus;
        public String ustatus_overtime;

    }
}
