package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/9/9 0009 10:45
 */

public class HomeCourseInfo {


    public ArrayList<CoursrInfo> data;
    public String                message;
    public int                   status;

    public class CoursrInfo {

        public String category_name;
        public String description;
        public String hits;
        public int    id;
        public String images;
        public String name;
        public int    status;


        @Override
        public String toString() {
            return "CoursrInfo{" +
                    "category_name='" + category_name + '\'' +
                    ", description='" + description + '\'' +
                    ", hits='" + hits + '\'' +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

}

