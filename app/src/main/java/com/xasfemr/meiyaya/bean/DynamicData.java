package com.xasfemr.meiyaya.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/13 0013 19:28
 */

public class DynamicData {

    public int                        code;
    public int                        status;
    public String                     message;
    public ArrayList<DynamicItemInfo> data;

    public static class DynamicItemInfo implements Serializable {

        public String            id;
        public String            userid;
        public String            content;
        public String            videourl;
        public String            datetime;
        public String            likes;
        public String            review;
        public String            share;
        public String            is_delete;
        public String            cat_name;
        public String            images1;
        public int               attention;
        public int               zan;
        public ArrayList<String> picture;

    }
}
