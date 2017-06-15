package com.ijustyce.fastandroiddev.baseLib.utils;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by yc on 16-2-6. 再次封装的Snackbar 类
 */
public class ISnackbar {

    private static ArrayList<String> notShouldWords; //   不显示的敏感词汇，比如服务端返回的token失效等，包含即不显示
    private static boolean showSnack = true;

    static {

        notShouldWords = new ArrayList<>();
    }

    /**
     * 添加敏感词汇
     * @param value 关键词
     */
    public static void addNotShowWorld(@NonNull String value){

        if (notShouldWords == null){
            notShouldWords = new ArrayList<>();
        }
        notShouldWords.add(value);
    }

    private static boolean isContains(@NonNull  String text){

        if (notShouldWords == null){
            return false;
        }

        for(String key : notShouldWords){
            if (text.contains(key)){
                return true;
            }
        }
        return false;
    }

    public static boolean shouldShow(@NonNull String text){

        return showSnack && !isContains(text);
    }

    /**
     *
     * @param view The view to find a parent from.
     * @param text The text to show.  Can be formatted text.
     * @return Snackbar object
     */
    public static Snackbar show(@NonNull View view, @NonNull String text) {

       return show(view, text, null, null);
    }

    /**
     *
     * @param view The view to find a parent from.
     * @param text The text to show.  Can be formatted text.
     * @param action action name , will show at right of Snackbar
     * @param listener a onClickListener
     * @return Snackbar object
     */
    public static Snackbar show(@NonNull View view, @NonNull String text,
                                    String action, View.OnClickListener listener){

        if (!shouldShow(text)){
            return null;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        if (action != null && listener != null) {
            snackbar.setAction(action, listener);
        }
        snackbar.show();
        return snackbar;
    }

    public static boolean isShowSnack() {
        return showSnack;
    }

    public static void setShowSnack(boolean showSnack) {
        ISnackbar.showSnack = showSnack;
    }
}
