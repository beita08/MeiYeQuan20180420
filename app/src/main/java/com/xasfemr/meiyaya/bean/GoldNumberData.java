package com.xasfemr.meiyaya.bean;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/12/20 0020 11:05
 */

public class GoldNumberData {

    /**
     * code : 200
     * status : 200
     * message : 查询成功
     * data : {"goldmoney":"215","withdraw":1,"bigmoney":10.75}
     */

    public int        code;
    public int        status;
    public String     message;
    public MyGoldInfo data;

    public static class MyGoldInfo {
        /**
         * goldmoney : 215
         * withdraw : 1
         * bigmoney : 10.75
         */

        public String goldmoney;
        public int    withdraw;
        public double bigmoney;
    }
}
