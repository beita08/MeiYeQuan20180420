package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/30 0030 11:26
 */

public class MoreIndustryNewsData {

    public int                     code;
    public int                     status;
    public String                  message;
    public ArrayList<MoreNewsInfo> data;

    public static class MoreNewsInfo {

        public String id;
        public String title;
        public String images;
        public String digest;
        public String time;
        public String hits;
    }

}
