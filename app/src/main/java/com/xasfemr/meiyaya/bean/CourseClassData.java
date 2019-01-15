package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/1/12 0012 16:46
 */

public class CourseClassData {

    public int                   code;
    public int                   status;
    public String                message;
    public ArrayList<FirstClass> data;

    public static class FirstClass {

        public String                 id;
        public String                 cname;
        public ArrayList<SecondClass> list;

        public static class SecondClass {

            public String id;
            public String cname;
            public String images;
            public String desc;
            public String net_typeid;
            public String url;
            public String url_channel;
        }
    }
}
