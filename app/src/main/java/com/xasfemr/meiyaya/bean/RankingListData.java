package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/29 0029 10:20
 */

public class RankingListData {

    public int      status;
    public String   message;
    public DataBean data;

    public static class DataBean {
        public ArrayList<ActiveUserInfo>   active;
        public ArrayList<TreasureUserInfo> treasure;

        public static class ActiveUserInfo {
            public String userid;
            public String username;
            public String images;
            public String live;
            public String type;
        }

        public static class TreasureUserInfo {

            public String userid;
            public String username;
            public String images;
            public String payment;
            public String type;
        }
    }
}
