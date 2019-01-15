package com.xasfemr.meiyaya.module.college.protocol;

import com.xasfemr.meiyaya.base.protocol.BaseProtocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/15.
 */

public class ExcellentListProtocol extends BaseProtocol implements Serializable{
        /**
         * id : 21
         * seeding_id : 1201
         * title : 李滢—员工招聘—美容院招人、培训
         * des : null
         * lecturer : 中医达人：李滢 \n 年轻态，更中医
         * lecturer_name : 李滢
         * icon : http://app.xasfemr.com/Public/Uploads/3-3.png
         * cover : http://app.xasfemr.com/Public/Uploads/3.jpg
         * fee : 限时免费
         * view : 648人学习
         * isdelete : 0
         * number : 1
         * title_all : 1、李滢—员工招聘—美容院招人、培训
         * list : [{"id":"1201","cid":"132857","channelname":"员工招聘","userid":"0","name":"李滢","cover":"http://app.xasfemr.com/Public/Uploads/2017-12-07/5a289c4a51edd.jpg","icon":"http://app.xasfemr.com/Public/Uploads/3-3.png","courseid":"2","coursename":"员工招聘","des":"    关注本节课，您将获得：99%的美容院老板不知道的招人渠道秘笈、用人留人秘笈！通过\u201c招才\u201d来\u201c招财\u201d，让你的美容院财源滚滚来！\r\n","title":"李滢\u2014员工招聘\u2014美容院招人、培训","ctime":"1512592202","vid":"70908078","video_name":"李滢\u2014员工招聘\u2014美容院招人、培训","origurl":"http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/p6Za2HVL_70908078_hd.mp4","orig_video_key":null,"begintime":null,"endtime":null,"view":"648","hot":"0","ismy":"0","is_member":"1","is_best":"1","fee":"限时免费"}]
         */

        public String id;
        public String seeding_id;
        public String title;
        public String des;
        public String lecturer;
        public String lecturer_name;
        public String icon;
        public String cover;
        public String fee;
        public String userid;
        public String lecturer_id;
        public String view;
        public String isdelete;
        public int number;
        public boolean iscollect;
        public String title_all;
        public ArrayList<ExcellentDiretoryProtocol> list;
//
//
//        public static class ExcellentCourseList implements Serializable{
//            /**
//             * id : 1201
//             * cid : 132857
//             * channelname : 员工招聘
//             * userid : 0
//             * name : 李滢
//             * cover : http://app.xasfemr.com/Public/Uploads/2017-12-07/5a289c4a51edd.jpg
//             * icon : http://app.xasfemr.com/Public/Uploads/3-3.png
//             * courseid : 2
//             * coursename : 员工招聘
//             * des :     关注本节课，您将获得：99%的美容院老板不知道的招人渠道秘笈、用人留人秘笈！通过“招才”来“招财”，让你的美容院财源滚滚来！
//
//             * title : 李滢—员工招聘—美容院招人、培训
//             * ctime : 1512592202
//             * vid : 70908078
//             * video_name : 李滢—员工招聘—美容院招人、培训
//             * origurl : http://vodb6jjcoqf.vod.126.net/vodb6jjcoqf/p6Za2HVL_70908078_hd.mp4
//             * orig_video_key : null
//             * begintime : null
//             * endtime : null
//             * view : 648
//             * hot : 0
//             * ismy : 0
//             * is_member : 1
//             * is_best : 1
//             * fee : 限时免费
//             */
//
//            public String id;
//            public String cid;
//            public String channelname;
//            public String userid;
//            public String name;
//            public String cover;
//            public String icon;
//            public String courseid;
//            public String coursename;
//            public String des;
//            public String title;
//            public String ctime;
//            public String vid;
//            public String video_name;
//            public String origurl;
//            public Object orig_video_key;
//            public Object begintime;
//            public Object endtime;
//            public String view;
//            public String hot;
//            public String ismy;
//            public String is_member;
//            public String is_best;
//            public String fee;
//
//        }
}
