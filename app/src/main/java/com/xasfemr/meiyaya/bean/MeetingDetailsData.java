package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/4/10 0010 10:37
 */

public class MeetingDetailsData {

    public int         code;
    public String      message;
    public MeetingInfo data;

    public static class MeetingInfo {

        public String id;
        public String userid;
        public String catid;
        public String areaid;
        public String title;
        public String content;
        public String thumb;
        public Object keywords;
        public Object description;
        public String linkman;
        public Object email;
        public Object qq;
        public String phone;
        public String address;
        public Object password;
        public Object mappoint;
        public Object postarea;
        public String postdate;
        public String enddate;
        public Object ip;
        public String click;
        public Object is_pro;
        public Object is_top;
        public Object top_type;
        public String is_check;
        public String longitude;
        public String latitude;
        public String is_start;
        public String business;
        public String icon;
        public int    attention;
        public String meetingPlace;
        public int    is_approve;
        public String meetingTime;
        public String organizer;
        public String share_link;
    }
}
