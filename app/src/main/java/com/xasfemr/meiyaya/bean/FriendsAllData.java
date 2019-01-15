package com.xasfemr.meiyaya.bean;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2018/2/26 0026 11:28
 */

public class FriendsAllData {

    public int        code;
    public int        status;
    public String     message;
    public AllFriends data;

    public static class AllFriends {

        public ArrayList<FriendInfo> attention;
        public ArrayList<FriendInfo>      fans;

        public static class FriendInfo {

            public String id;
            public String uid;
            public String userid;
            public String cat_name;
            public String images1;
        }
    }
}
