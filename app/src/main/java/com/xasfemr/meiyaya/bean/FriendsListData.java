package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/12/1 0001 10:45
 */
public class FriendsListData {

    public int                   status;
    public String                message;
    public ArrayList<FriendInfo> data;

    public static class FriendInfo {
        public String id;
        public String uid;
        public String userid;
        public String cat_name;
        public String images1;
    }
}
