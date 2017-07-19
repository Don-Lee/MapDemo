package cn.rjgc.mapdemo.utils;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Don on 2017/7/14.
 */

public class Utils {
    private static SimpleDateFormat sdf = null;
    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "null" : sdf.format(l);
    }
}
