package com.xasfemr.meiyaya.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageUtils {

    //获取版本名称
    public static String getVersionName(Context ctx) {
        //包管理器
        PackageManager pm = ctx.getPackageManager();
        try {

            //获取包的基本信息; 参1:包名; 参2:0表示不需要额外信息
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            String versionName = packageInfo.versionName;//版本名
            int versionCode = packageInfo.versionCode;//版本号

            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    //获取版本号
    public static int getVersionCode(Context ctx) {
        //包管理器
        PackageManager pm = ctx.getPackageManager();
        try {

            //获取包的基本信息; 参1:包名; 参2:0表示不需要额外信息
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            String versionName = packageInfo.versionName;//版本名
            int versionCode = packageInfo.versionCode;//版本号

            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
