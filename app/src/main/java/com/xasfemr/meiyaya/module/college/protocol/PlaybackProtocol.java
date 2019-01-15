package com.xasfemr.meiyaya.module.college.protocol;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PlaybackProtocol {


    /**
     * typeName : 直播录制
     * createTime : 1511421176352
     * duration : 0:0:8
     * origUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4
     * downloadOrigUrl : http://vodb6jjcoqf.nosdn.127.net/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4?download=%E7%BE%8E%E5%91%80%E5%91%80%E7%9B%B4%E6%92%AD%E9%97%B47_20171123-151256_20171123-151305.mp4
     * status : 2
     * updateTime : 1511421176352
     * description : null
     * snapshotUrl : http://vodb6jjcoqf.nosdn.127.net/5b864103-805e-443f-a6f0-eaf9d7f27d2c.jpg
     * initialSize : 654108
     * videoName : 美页圈直播间7_20171123-151256_20171123-151305
     * typeId : 132585
     * completeTime : null
     * vid : 54056504
     * durationMsec : 8000
     * type : morehot
     * status1 : 精彩回放
     * cid : 9aef6cd2631d4b35bbe8881053839b3a
     * userid : 74
     * username : null
     * icon : http://app.xasfemr.com/Public
     * cover : http://app.xasfemr.com/Public/Uploads/2017-11-23/5a1674c76b089.png
     * courseid : 13
     * des : 标准划分：按照严格标准准确划分美容行业的不同盈利种类，为经营者科普支撑店面正常运营的基础理论知识，整理思路，全面了解行业内信息，知己知彼，百战不殆。
     * title : 重口味
     * view : 9
     * addr : {"httpPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4","hlsPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4","rtmpPullUrl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4"}
     */


    public String duration;

    public int status;

    public String type;
    public String status1;
    public String cid;
    public String userid;
    public String username;
    public String icon;
    public String cover;
    public String courseid;
    public String des;
    public String title;
    public String view;
    public String coursename;
    public String ismy;
    public String vid;
    public AddrBean addr;


    public static class AddrBean {
        /**
         * httpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4
         * hlsPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4
         * rtmpPullUrl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/9aef6cd2631d4b35bbe8881053839b3a_1511421176352_1511421185157_2621270-00004.mp4
         */

        public String httpPullUrl;
        public String hlsPullUrl;
        public String rtmpPullUrl;

    }
}
