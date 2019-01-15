package com.xasfemr.meiyaya.bean;

/**
 * Company : 西安妙云电子商务有限公司
 */

public class ChatItem {

    public String myid;
    public String friendid;
    public String friendicon;
    public String friendname;
    public int    type;
    public String content;
    public long   time;
    public int    status;

    public ChatItem(String myid, String friendid, String friendicon, String friendname, int type, String contet, long time, int readStatus) {
        this.myid = myid;
        this.friendid = friendid;
        this.friendicon = friendicon;
        this.friendname = friendname;
        this.type = type;
        this.content = contet;
        this.time = time;
        this.status = readStatus;
    }
}
