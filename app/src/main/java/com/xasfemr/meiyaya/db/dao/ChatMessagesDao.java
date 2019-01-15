package com.xasfemr.meiyaya.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xasfemr.meiyaya.bean.ChatItem;
import com.xasfemr.meiyaya.bean.UnreadMessageList;
import com.xasfemr.meiyaya.db.ChatMessagesOpenHelper;

import java.util.ArrayList;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/26 0026 11:42
 */

public class ChatMessagesDao {

    private final ChatMessagesOpenHelper mOpenHelper;

    //3.选择懒汉模式
    private static ChatMessagesDao mInstance;

    //1.私有构造方法
    private ChatMessagesDao(Context context) {
        mOpenHelper = new ChatMessagesOpenHelper(context);
    }

    //2.公开方法,返回单例对象
    public static ChatMessagesDao getInstance(Context context) {
        //懒汉: 考虑线程安全问题, 两种方式: 1. 给方法加同步锁 synchronized, 效率低; 2. 给创建对象的代码块加同步锁
        if (mInstance == null) {
            synchronized (ChatMessagesDao.class) {

                if (mInstance == null) {
                    mInstance = new ChatMessagesDao(context);
                }
            }
        }

        return mInstance;

    }

    /*"msg", "from_uid", "from_icon", "from_name", "to_uid", "sendtime" */

    //myid varchar(10), friendid varchar(10),friendicon varchar(200),friendname varchar(100), type integer, content varchar(100), time integer, status integer
    public boolean insert(String myid, String friendid, String friendicon, String friendname, int type, String content, long time, int status) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("myid", myid);
        values.put("friendid", friendid);
        values.put("friendicon", friendicon);
        values.put("friendname", friendname);
        values.put("type", type);
        values.put("content", content);
        values.put("time", time);
        values.put("status", status);

        long rowID = db.insert("chatmsg", null, values);

        db.close();

        return rowID != -1;

    }

    public boolean delete(String myid, String friendid) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int deleteRows = db.delete("chatmsg", "myid=? and friendid=?", new String[]{myid, friendid});

        db.close();

        return deleteRows > 0;

    }

    public boolean update(String myid, String friendid, int status) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", status);

        int updateRows = db.update("chatmsg", values, "myid=? and friendid=?", new String[]{myid, friendid});

        db.close();

        return updateRows > 0;

    }

    //查询某两个自己与某好友的所有聊天记录,在ChatActivity展示
    public ArrayList<ChatItem> query(String myid, String friendid) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = db.query("chatmsg", new String[]{"myid", "friendid", "friendicon", "friendname", "type", "content", "time", "status"}, "myid=? and friendid=?", new String[]{myid, friendid}, null, null, "time");

        ArrayList<ChatItem> chatList = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                String myId = cursor.getString(cursor.getColumnIndex("myid"));
                String friendId = cursor.getString(cursor.getColumnIndex("friendid"));
                String friendIcon = cursor.getString(cursor.getColumnIndex("friendicon"));
                String friendName = cursor.getString(cursor.getColumnIndex("friendname"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                int status = cursor.getInt(cursor.getColumnIndex("status"));

                ChatItem chatItem = new ChatItem(myId, friendId, friendIcon, friendName, type, content, time, status);

                chatList.add(chatItem);
            }
            cursor.close();
        }
        db.close();

        return chatList;
    }

    //查询自己与所有好友的最新一条聊天记录,在消息列表展示
    public ArrayList<UnreadMessageList.UnreadMessage> queryAllNewest(String myid) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor cursor = db.query("chatmsg", new String[]{"myid", "friendid", "friendicon", "friendname", "type", "content", "time", "status"}, "myid=?", new String[]{myid}, null, null, "time desc"); //时间降序查询

        ArrayList<UnreadMessageList.UnreadMessage> msgList = new ArrayList<>();
        ArrayList<String> friendIdList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String friendId = cursor.getString(cursor.getColumnIndex("friendid"));
                if (!friendIdList.contains(friendId)) {
                    friendIdList.add(friendId);

                    String myId = cursor.getString(cursor.getColumnIndex("myid"));
                    String friendIcon = cursor.getString(cursor.getColumnIndex("friendicon"));
                    String friendName = cursor.getString(cursor.getColumnIndex("friendname"));
                    String content = cursor.getString(cursor.getColumnIndex("content"));
                    long time = cursor.getLong(cursor.getColumnIndex("time"))/1000;

                    UnreadMessageList.UnreadMessage readMessage = new UnreadMessageList.UnreadMessage(0, myId, friendId, null, friendName, friendIcon, content, time);

                    msgList.add(readMessage);
                }
            }
            cursor.close();
        }
        db.close();

        return msgList;
    }

}