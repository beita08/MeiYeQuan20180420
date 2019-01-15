package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/30 0030 17:32
 */

public class UnreadMessageData {

    public int                      code;
    public String                   message;
    public ArrayList<UnreadMessage> data;

    public static class UnreadMessage {

        public String uncount;
        public String to_uid;
        public String from_uid;
        public String to_uidname;
        public String from_uidname;
        public String icon;
        public String msg;
        public String sendtime;
    }
}
