package com.xasfemr.meiyaya.module.home.protocol;

import java.util.List;

/**
 * Created by sen.luo on 2018/2/7.
 */

public class HomeBannerProtocol {
    /**
     * banner : [{"id":"5","images":"http://testapp.xasfemr.com/Public/Uploads/2017-12-20/5a3a30ff1d649.png","time":"2017-12-20 17:44:31","type":"3","title":"邀请好友注册返利","url":"http://app.xasfemr.com/index.php?m=Home&c=Activity&a=invite","is_delete":"0"},{"id":"4","images":"http://testapp.xasfemr.com/Public/Uploads/2017-12-12/5a2f875ce7745.png","time":"2017-12-12 15:38:04","type":"3","title":"新用户送30天会员","url":"http://app.xasfemr.com/index.php?m=Home&c=Activity&a=bannerdetails","is_delete":"0"},{"id":"3","images":"http://testapp.xasfemr.com/Public/Uploads/2017-11-09/5a03ce387fb7f.png","time":"2017-11-09 11:40:40","type":"3","title":"轻松做美业","url":"http://app.xasfemr.com/index.php?m=Home&c=Activity&a=couseall","is_delete":"0"},{"id":"2","images":"http://testapp.xasfemr.com/Public/Uploads/2017-11-09/5a03ce44d9211.png","time":"2017-11-09 11:40:52","type":"3","title":"线上直播课程","url":"","is_delete":"0"},{"id":"1","images":"http://testapp.xasfemr.com/Public/Uploads/2017-11-09/5a03ce5002242.png","time":"2017-11-09 11:41:04","type":"3","title":"养生之道","url":"http://app.xasfemr.com/index.php?m=Home&c=Activity&a=yszhidao","is_delete":"0"}]
     * activity : {"id":"6","images":"http://testapp.xasfemr.com/Public/Uploads/2018-03-09/5aa2572e8f695.png","time":"2018-03-09 17:43:10","type":"2","title":"","url":"http://app.xasfemr.com/index.php?m=Home&c=Activity&a=invite","is_delete":"0"}
     */

    public List<ActivityBean> activity;
    public List<BannerBean> banner;



    public static class ActivityBean {

        public String id;
        public String content;
        public String title;
        public String userid;




    }

    public static class BannerBean {
        /**
         * id : 5
         * images : http://testapp.xasfemr.com/Public/Uploads/2017-12-20/5a3a30ff1d649.png
         * time : 2017-12-20 17:44:31
         * type : 3
         * title : 邀请好友注册返利
         * url : http://app.xasfemr.com/index.php?m=Home&c=Activity&a=invite
         * is_delete : 0
         */

        public String id;
        public String images;
        public String time;
        public String type;
        public String title;
        public String url;
        public String is_delete;


    }
//    public String id;
//    public String images;
//    public String time;
//    public String type;
//    public String title;
//    public String url;
//    public String is_delete;






}
