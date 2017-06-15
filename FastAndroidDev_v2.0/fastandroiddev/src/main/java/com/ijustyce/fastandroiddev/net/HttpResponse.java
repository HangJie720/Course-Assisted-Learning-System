package com.ijustyce.fastandroiddev.net;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ijustyce.fastandroiddev.baseLib.utils.DateUtil;
import com.ijustyce.fastandroiddev.baseLib.utils.StringUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yc on 2015/8/14.  httpResponse
 */
public class HttpResponse {

    private String url;
    private HttpListener httpListener;
    private int cacheTime;
    private String cacheKey;
    private static Map<String, String> responseData;

    private Request request;

    static {
        responseData = new HashMap<>();
    }

    public void setRequest(Request request){

        this.request = request;
    }

    public HttpResponse(int cacheTime, String cacheKey, String url, HttpListener httpListener){

        this.url = url;
        this.httpListener = httpListener;
        this.cacheTime = cacheTime;
        this.cacheKey = cacheKey;
    }

    private void saveCache(String response){

        if (cacheTime > 0){

            responseData.put(cacheKey, DateUtil.getTimesTamp() + response);
        }
    }

    public static void removeCache(String url){

        if (url == null){
            responseData.clear();
        }else {
            responseData.remove(url);
        }
    }

    public static String getCache(int cacheTime, String url){

        String tmp = responseData.get(url);
        if (tmp == null || tmp.length() < 10){
            return null;
        }
        long putTime = StringUtils.getLong(tmp.substring(0, 10));   //  时间戳只有10位
        if (cacheTime < DateUtil.getTimesTamp() - putTime){
            responseData.remove(url);
            return null;
        }
        return tmp.substring(10);
    }

    public Response.Listener<JSONObject> jsonListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {

            if (request != null && request.isCanceled()){

                return;
            }

            saveCache(String.valueOf(jsonObject));
            if (httpListener != null){
                httpListener.success(String.valueOf(jsonObject), url);
            }
        }
    };

    public Response.Listener<String> stringListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {

            if (request != null && request.isCanceled()){

                return;
            }
            if (httpListener != null){

                if (s == null){
                    httpListener.fail(-2, "response is null", url);
                }else{
                    saveCache(s);
                    httpListener.success(s, url);
                }
            }
        }
    };

    public Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError volleyError) {

            if (request != null && request.isCanceled()){

                return;
            }

            if (httpListener != null){
                httpListener.fail(-1, "request failed", url);
            }
        }
    };
}
