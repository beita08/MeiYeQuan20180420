package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/9 0009 9:45
 */

public class UserPostInfoData {

    public int                     code;
    public String                  message;
    public ArrayList<UserPostInfo> data;

    public static class UserPostInfo {
        public String id;
        public String userid;
        public int    catid;
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
        public Object address;
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
        public double longitude;
        public double latitude;
        public int    is_start;
        public String classify;
        public int    is_approve;
        public String price;
        public String oldprice;
        public String postage;
        public String company;
        public String position;
        public String sex;
        public String age;
        public String yearWork;
        public String expect_salary;
        public String companyName;
        public String jobName;
        public String workPlace;
        public String salary;
        public String business;
        public String distance;
        public String meetingPlace;
        public String meetingTime;
        public String workExperience;
        public String personalSummary;
    }
}
