package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/2 0002 17:12
 */

public class LiveChannelUrlData {

    public int      code;
    public String   message;
    public DataBean data;

    public static class DataBean {

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
        public AddrBean addr;
        public UserBean user;

        public static class AddrBean {

            public String httpPullUrl;
            public String hlsPullUrl;
            public String rtmpPullUrl;
            public String name;
            public String pushUrl;

        }

        public static class UserBean {

            public String userid;
            public String username;
            public String icon;
            public String courseid;
            public String des;
            public String title;

        }
    }
}
