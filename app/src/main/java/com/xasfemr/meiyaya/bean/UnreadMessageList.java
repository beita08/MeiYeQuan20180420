package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/30 0030 17:32
 */

public class UnreadMessageList {

    public int                      code;
    public String                   message;
    public ArrayList<UnreadMessage> data;

    public static class UnreadMessage {

        public int    uncount;
        public String to_uid;
        public String from_uid;
        public String to_uidname;
        public String from_uidname;
        public String from_icon;
        public String msg;
        public long   sendtime;

        public UnreadMessage(int uncount, String to_uid, String from_uid, String to_uidname, String from_uidname, String from_icon, String msg, long sendtime) {
            this.uncount = uncount;
            this.to_uid = to_uid;
            this.from_uid = from_uid;
            this.to_uidname = to_uidname;
            this.from_uidname = from_uidname;
            this.from_icon = from_icon;
            this.msg = msg;
            this.sendtime = sendtime;
        }
    }
}
