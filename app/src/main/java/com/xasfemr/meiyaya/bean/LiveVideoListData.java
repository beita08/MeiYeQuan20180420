package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/3 0003 9:39
 */

public class LiveVideoListData {

    public int      code;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public int                 pnum;
        public int                 totalRecords;
        public int                 totalPnum;
        public int                 records;
        public ArrayList<LiveInfo> list;

        public static class LiveInfo {

            public int      needRecord;
            public int      uid;
            public int      duration;
            public int      status;
            public String   name;
            public String   filename;
            public int      format;
            public int      type;
            public long     ctime;
            public String   cid;
            public Object   recordStatus;
            public String   status1;
            public String   userid;
            public String   username;
            public String   icon;
            public String   cover;
            public String   courseid;
            public String   des;
            public String   title;
            public AddrBean addr;

            public static class AddrBean {

                public String httpPullUrl;
                public String hlsPullUrl;
                public String rtmpPullUrl;
                public String name;
                public String pushUrl;
            }
        }
    }
}
