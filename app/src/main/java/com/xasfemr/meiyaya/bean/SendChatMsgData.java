package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/12/13 0013 15:34
 */

public class SendChatMsgData {

    public int      code;
    public String   message;
    public DataBean data;

    public static class DataBean {

        public int    code;
        public String msg;
        public String from_uid;
        public String from_icon;
        public String from_name;
        public String to_uid;
        public String to_icon;
        public String to_name;
        public long   sendtime;

    }
}
