package com.xasfemr.meiyaya.bean;

import java.util.List;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/11 0011 10:36
 */

public class MoreHotCourseData {

    public int      code;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public int            pageSize;
        public int            totalRecords;
        public int            currentPage;
        public int            pageNum;
        public List<ListBean> list;

        public static class ListBean {

            public long   createTime;
            public String hdMp4Url;
            public String origUrl;
            public String downloadOrigUrl;
            public long   updateTime;
            public int    status;
            public String videoName;
            public String downloadHdMp4Url;
            public String typeName;
            public String duration;
            public String description;
            public String snapshotUrl;
            public int    hdMp4Size;
            public int    initialSize;
            public int    vid;
            public long   completeTime;
            public int    typeId;
            public int    durationMsec;
            public String status1;
            public String cid;
            public String userid;
            public String username;
            public String icon;
            public String cover;
            public String courseid;
            public String des;
            public String title;
            public String view;
        }
    }
}

/*

public class MoreMemberCourseData {

    public int            code;
    public String         message;
    public List<DynamicItemInfo> data;

    public static class DynamicItemInfo {

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


*/
