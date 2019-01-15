package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/12/4 0004 10:54
 */

public class GoldDetailListData {

    public int                 code;
    public String              message;
    public ArrayList<GoldChangeInfo> data;

    public static class GoldChangeInfo {

        public String userid;
        public String out_trade_no;
        public String trade_no;
        public double total_fee;
        public int    goldmoney;
        public String subject;
        public String notify_time;
        public String username;
        public String type;
        public String sgin;
    }
}
