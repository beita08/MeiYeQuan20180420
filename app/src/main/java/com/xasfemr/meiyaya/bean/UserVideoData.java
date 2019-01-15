package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/3/12 0012 9:58
 */

public class UserVideoData {
    public int                  code;
    public String               message;
    public ArrayList<VideoInfo> data;

    public static class VideoInfo {
        public String   id;
        public String   cid;
        public String   channelname;
        public String   userid;
        public String   name;
        public String   cover;
        public String   icon;
        public String   courseid;
        public String   coursename;
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
        public String   ismy;
        public String   is_member;
        public String   is_best;
        public String   type;
        public String   status1;
        public int      status;
        public String   username;
        public String   duration;
        public AddrBean addr;

        public static class AddrBean {
            public String httpPullUrl;
            public String hlsPullUrl;
            public String rtmpPullUrl;
        }
    }
}
