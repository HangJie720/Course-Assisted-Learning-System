package com.ijustyce.fastandroiddev.baseLib.utils;

/**
 * Created by yc on 15-12-24.
 */
public class StringUtils {

    public static boolean isEmpty(String text){

        return text == null || text.replaceAll(" ", "").length() == 0;
    }

    public static int getInt(String value, int defaultValue){

        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static int getInt(String value){

        return getInt(value, 0);
    }

    public static long getLong(String value){

        try {
            return Long.parseLong(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
