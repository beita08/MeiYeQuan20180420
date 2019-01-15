package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/26 0026 18:11
 */

public class HomeBannerDate {

    public int                  code;
    public String               message;
    public ArrayList<BannerImg> data;

    public static class BannerImg {

        public String id;
        public String images;
        public String time;
        public String type;
        public String title;
        public String url;
        public String is_delete;
    }
}
