package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/4/13 0013 15:28
 */

public class MeetingApplyData {

    public int              code;
    public String           message;
    public MeetingApplyList data;

    public static class MeetingApplyList {

        public int                  count;
        public ArrayList<ApplyInfo> list;

        public static class ApplyInfo {
            public String id;
            public String infoid;
            public String userid;
            public String username;
            public String phone;
            public String meetingnumber;
            public String position;
            public String companyname;
            public String content;
            public String is_check;
            public String postdate;
            public String ip;
            public String icon;
        }
    }


}
