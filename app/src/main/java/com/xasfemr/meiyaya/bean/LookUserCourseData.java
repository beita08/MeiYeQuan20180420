package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/21 0021 16:24
 */

public class LookUserCourseData {

    public int      code;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public int                        pageSize;
        public int                        totalRecords;
        public int                        currentPage;
        public int                        pageNum;
        public ArrayList<CourseVideoInfo> list;

        public static class CourseVideoInfo {

            public String   id;
            public String   cid;
            public String   channelname;
            public String   userid;
            public String   name;
            public String   cover;
            public String   icon;
            public String   courseid;
            public String   des;
            public String   title;
            public String   ctime;
            public String   vid;
            public String   video_name;
            public String   origurl;
            public String   orig_video_key;
            public String   begintime;
            public String   endtime;
            public String   view;
            public String   hot;
            public String   type;
            public String   status1;
            public int      status;
            public String   username;
            public String   duration;
            public AddrBean addr;
            public String coursename;

            public static class AddrBean {

                public String httpPullUrl;
                public String hlsPullUrl;
                public String rtmpPullUrl;
            }
        }
    }
}
