package com.xasfemr.meiyaya.bean;

import java.util.List;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/11 0011 11:03
 */

public class MoreMemberCourseData {

    public int            code;
    public String         message;
    public List<DataBean> data;

    public static class DataBean {

        public String id;
        public String cid;
        public String channelname;
        public String userid;
        public String name;
        public String cover;
        public String icon;
        public String courseid;
        public String des;
        public String title;
        public String ctime;
        public String vid;
        public String video_name;
        public String origurl;
        public String orig_video_key;
        public String begintime;
        public String endtime;
        public String view;
        public String duration;

    }
}
