package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/16 0016 18:04
 */

public class DynamicCommentData {

    public int                    status;
    public String                 message;
    public ArrayList<CommentInfo> data;

    public static class CommentInfo {

        public String id;
        public String content;
        public String datetime;
        public String userid;
        public String cat_name;
        public String images1;
    }
}
