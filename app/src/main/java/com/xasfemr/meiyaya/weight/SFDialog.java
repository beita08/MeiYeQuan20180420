package com.xasfemr.meiyaya.weight;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Administrator on 2017/11/30.
 */

public class SFDialog {



    public static void  basicDialog(Context context, String title, String message, DialogInterface.OnClickListener negativeClickListener){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("确定", negativeClickListener);
        builder.setPositiveButton("取消" ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    //只有确认按钮的Dialog
    public static void onlyConfirmDialog(Context context, String title, String message, DialogInterface.OnClickListener negativeClickListener){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("确定", negativeClickListener);
        builder.create().show();
    }
}
