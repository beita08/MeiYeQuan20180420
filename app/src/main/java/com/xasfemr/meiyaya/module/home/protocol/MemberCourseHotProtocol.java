package com.xasfemr.meiyaya.module.home.protocol;

/**
 * Created by Administrator on 2017/11/29.
 */

public class MemberCourseHotProtocol {


    public long createTime;
    public String hdMp4Url;
    public String origUrl;
    public String downloadOrigUrl;
    public long updateTime;
    public int status;
    public String videoName;
    public String downloadHdMp4Url;
    public String typeName;
    public String duration;
    public String description;
    public String snapshotUrl;
    public int hdMp4Size;
    public int initialSize;
    public String vid;
    public long completeTime;
    public int typeId;
    public int durationMsec;
    public String type;
    public String status1;
    public String cid;
    public String userid;
    public String username;
    public String icon;
    public String cover;
    public String courseid;
    public String coursename;
    public String des;
    public String title;
    public String ismy;
    public String view;
    public AddrBean addr;



    public static class AddrBean {
        /**
         * httpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/0pvkFj5p_44487668_hd.mp4
         * hlsPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/0pvkFj5p_44487668_hd.mp4
         * rtmpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/0pvkFj5p_44487668_hd.mp4
         */

        public String httpPullUrl;
        public String hlsPullUrl;
        public String rtmpPullUrl;

    }
}
