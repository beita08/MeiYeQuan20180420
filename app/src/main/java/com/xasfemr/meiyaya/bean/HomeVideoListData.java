package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/7 0007 16:21
 */
public class HomeVideoListData {

    public int                      code;
    public String                   message;
    public ArrayList<HomeVideoInfo> data;

    public static class HomeVideoInfo {

        public String   type;
        public String   status1;
        public int      status;
        public String   cid;
        public String   userid;
        public String   username;
        public String   icon;
        public String   cover;
        public String   courseid;
        public String   coursename;
        public String   des;
        public String   title;
        public String   view;
        public AddrBean addr;
        public String   vid;
        public String   duration;
        public String   ismy; // 0 自营   1 第三方录制

        public static class AddrBean {

            public String httpPullUrl;
            public String hlsPullUrl;
            public String rtmpPullUrl;
        }
    }


}
