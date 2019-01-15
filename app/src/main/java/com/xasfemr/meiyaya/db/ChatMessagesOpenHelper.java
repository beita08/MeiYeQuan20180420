package com.xasfemr.meiyaya.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Description:
 * Company    : shifeier
 * Author     : liuxb
 * Date       : 2017/10/26 0026 11:13
 */

public class ChatMessagesOpenHelper extends SQLiteOpenHelper {


    public ChatMessagesOpenHelper(Context context) {
        super(context, "chatmessages.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表  myid, friendid, friendicon, friendname, type, content, time, status
        String sql = "create table chatmsg(_id integer primary key autoincrement, myid varchar(10), friendid varchar(10),friendicon varchar(200),friendname varchar(100), type integer, content varchar(100), time integer, status integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}