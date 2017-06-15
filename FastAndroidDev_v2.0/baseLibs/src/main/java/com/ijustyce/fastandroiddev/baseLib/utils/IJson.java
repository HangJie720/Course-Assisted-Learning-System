package com.ijustyce.fastandroiddev.baseLib.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by yc on 15-12-23.   再次封装的 GSon 类
 */
public class IJson {

    private static Gson gson;  //  请尽量用单例，效率高

    static {

        gson = new Gson();
    }

    /**
     *  把对象转为json
     * @param object 某个对象
     * @param type 类型
     * @return json string
     */
    public static String toJson(Object object, Type type){

        String string = null;
        try{
            string = gson.toJson(object, type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 将json 转为对象
     * @param jsonString
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String jsonString, Type type){

        T tmp = null;
        try{
            tmp = gson.fromJson(jsonString, type);
        }catch (Exception e){
            e.printStackTrace();
        }
        return tmp;
    }
}
