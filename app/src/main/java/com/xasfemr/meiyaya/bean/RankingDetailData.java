package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/11/29 0029 14:56
 */

public class RankingDetailData {

    public int                        status;
    public String                     message;
    public ArrayList<RankingUserInfo> data;

    public static class RankingUserInfo {

        public String userid;
        public String username;
        public String images;
        public String live;
        public String payment;
        public String type;
    }
}
