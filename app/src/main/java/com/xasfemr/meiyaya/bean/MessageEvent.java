package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/30 0030 16:18
 */

public class MessageEvent {

    public String myid;
    public String friendid;
    public String friendicon;
    public String friendname;
    public int    type;
    public String content;
    public long   time;
    public int    status;

    public MessageEvent(String myid, String friendid, String friendicon, String friendname, int type, String content, long time, int status) {
        this.myid = myid;
        this.friendid = friendid;
        this.friendicon = friendicon;
        this.friendname = friendname;
        this.type = type;
        this.content = content;
        this.time = time;
        this.status = status;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "myid='" + myid + '\'' +
                ", friendid='" + friendid + '\'' +
                ", friendicon='" + friendicon + '\'' +
                ", friendname='" + friendname + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", status=" + status +
                '}';
    }
}



