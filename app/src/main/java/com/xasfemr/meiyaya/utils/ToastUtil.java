package com.xasfemr.meiyaya.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * <p>Description: Toast统一管理类<／p>

 */
public class ToastUtil {

    private static Toast sToast;

    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, CharSequence message) {
        if(!TextUtils.isEmpty(message)){
            if (sToast == null) {
                sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            } else {
                sToast.setText(message);
                sToast.setDuration(Toast.LENGTH_SHORT);
            }
            sToast.show();
        }
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(Context context, int message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
            sToast.setDuration(Toast.LENGTH_SHORT);
        }
        sToast.show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, CharSequence message) {
        if(!TextUtils.isEmpty(message)) {
            if (sToast == null) {
                sToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            } else {
                sToast.setText(message);
                sToast.setDuration(Toast.LENGTH_SHORT);
            }
            sToast.show();
        }
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(Context context, int message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        } else {
            sToast.setText(message);
            sToast.setDuration(Toast.LENGTH_SHORT);
        }
        sToast.show();
    }
}