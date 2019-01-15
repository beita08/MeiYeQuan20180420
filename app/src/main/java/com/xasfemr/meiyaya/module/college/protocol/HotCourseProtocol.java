package com.xasfemr.meiyaya.module.college.protocol;

/**
 * Created by Administrator on 2017/12/4.
 */

public class HotCourseProtocol {


    /**
     * id : 511
     * cid : 132846
     * channelname : 五行养生
     * userid : 0
     * name : 美页圈讲师
     * cover : http://vodb6jjcoqf.nosdn.127.net/7cc7683b-d30d-4151-b025-00412b7d290a.jpg
     * icon : http://app.xasfemr.com/Public
     * courseid : 20
     * coursename : 五行养生
     * des : 教大家认识中医中的肝，讲述肝的生理功能及肝方面常见疾病和症状的诊断，主讲女性肝功能不好导致的一些症状和生活中关于肝...
     * title : 《五脏养生：肝》
     * ctime : 1510740304
     * vid : 44458034
     * video_name : 《五脏养生：肝》
     * origurl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4
     * orig_video_key : null
     * begintime : null
     * endtime : null
     * view : 699次
     * hot : 1
     * ismy : 0
     * is_member : 1
     * type : moremember
     * status1 : 精彩回放
     * status : 2
     * username : 美页圈讲师
     * duration : 00:21:38
     * addr : {"httpPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4","hlsPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4","rtmpPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4"}
     */

    public String id;
    public String cid;
    public String channelname;
    public String userid;
    public String name;
    public String cover;
    public String icon;
    public String courseid;
    public String coursename;
    public String des;
    public String title;
    public String ctime;
    public String vid;
    public String video_name;
    public String origurl;
    public Object orig_video_key;
    public Object begintime;
    public Object endtime;
    public String view;
    public String hot;
    public String ismy;
    public String is_member;
    public String type;
    public String status1;
    public int status;
    public String username;
    public String duration;
    public AddrBean addr;


    public static class AddrBean {
        /**
         * httpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4
         * hlsPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4
         * rtmpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/iwdwDBCG_44458034_hd.mp4
         */

        public String httpPullUrl;
        public String hlsPullUrl;
        public String rtmpPullUrl;
    }
}
