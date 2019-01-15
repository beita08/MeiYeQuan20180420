package com.xasfemr.meiyaya.module.college.protocol;

/**
 * Created by Administrator on 2017/11/23.
 */

public class CourseProtocol {

    /**
     * needRecord : 1
     * uid : 79252
     * duration : 120
     * status : 1
     * name : 美页圈直播间19
     * filename : 美页圈直播间19
     * format : 0
     * type : 0
     * ctime : 1509587113327
     * cid : f29bd773b778456e97436042ffe4bed9
     * recordStatus : null
     * status1 : 直播中
     * userid : 74
     * username : null
     * icon : http://app.xasfemr.com/Public
     * cover : http://app.xasfemr.com/Public/Uploads/2017-11-23/5a168579190b3.png
     * courseid : 13
     * des : 标准划分：按照严格标准准确划分美容行业的不同盈利种类，为经营者科普支撑店面正常运营的基础理论知识，整理思路，全面了解行业内信息，知己知彼，百战不殆。
     * title : 测试
     * view : 1
     * addr : {"httpPullUrl":"http://flvbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9.flv?netease=flvbd01adb2.live.126.net","hlsPullUrl":"http://pullhlsbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9/playlist.m3u8","rtmpPullUrl":"rtmp://vbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9","name":"美页圈直播间19","pushUrl":"rtmp://pbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9?wsSecret=2396fbdffa3687879a60b80ecc583a12&wsTime=1511425396"}
     */

    public int needRecord;
    public int uid;
    public String duration;
    public int status;
    public String name;
    public String filename;
    public int format;
    public int type;
    public long ctime;
    public String cid;
    public Object recordStatus;
    public String status1;
    public String userid;
    public String username;
    public String icon;
    public String cover;
    public String courseid;
    public String des;
    public String coursename;
    public String title;

    public String ismy;

    public String view;
    public AddrBean addr;


    public static class AddrBean {
        /**
         * httpPullUrl : http://flvbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9.flv?netease=flvbd01adb2.live.126.net
         * hlsPullUrl : http://pullhlsbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9/playlist.m3u8
         * rtmpPullUrl : rtmp://vbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9
         * name : 美页圈直播间19
         * pushUrl : rtmp://pbd01adb2.live.126.net/live/f29bd773b778456e97436042ffe4bed9?wsSecret=2396fbdffa3687879a60b80ecc583a12&wsTime=1511425396
         */

        public String httpPullUrl;
        public String hlsPullUrl;
        public String rtmpPullUrl;
        public String name;
        public String pushUrl;

    }
}
