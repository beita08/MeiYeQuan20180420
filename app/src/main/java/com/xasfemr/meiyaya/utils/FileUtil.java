package com.xasfemr.meiyaya.utils;

import android.content.Context;

import java.io.File;

public class FileUtil {

    public static File getSaveFile(Context context, String name) {
        return new File(context.getFilesDir(), name + "_pic.jpg");
    }

}
