package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/10 0010 16:27
 */

public class HomeIndustryData {

    public int                     code;
    public int                     status;
    public String                  message;
    public ArrayList<IndustryInfo> data;

    public static class IndustryInfo {

        public String id;
        public String title;
        public String images;
        public String time;
        public String digest;
        public String hits;
    }
}
