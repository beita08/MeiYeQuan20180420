package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/13 0013 11:09
 */

public class CollegeLectureData {

    public int    status;
    public String message;

    public ArrayList<LectureItemData> data;

    public class LectureItemData {

        public String datetime;
        public String images;
        public String    fans;
        public String    id;
        public String    integal;
        public String    is_delete;
        public String    live;
        public String    mid;
        public String    payment;
        public String    roomid;
        public String    specialist;
        public String signture;
        public String username;

    }
}
