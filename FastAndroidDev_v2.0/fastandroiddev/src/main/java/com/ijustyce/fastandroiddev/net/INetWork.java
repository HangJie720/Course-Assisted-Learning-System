package com.ijustyce.fastandroiddev.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;
import com.ijustyce.fastandroiddev.baseLib.utils.ToastUtil;

import java.util.Map;

/**
 * Created by yc on 2015/8/12.  负责发送网络请求的类
 */
public final class INetWork {

    private static boolean showToast = true;

    /**
     * whether show ToastUtil , if in thread , please disable it
     *
     * @param value
     */
    public static void showToast(boolean value) {

        showToast = value;
    }

    /**
     * 移除某个url对应的缓存，或者清空所有缓存
     * @param url   url，如果为null，则移除所有
     */
    public static void removeCache(String url){

        HttpResponse.removeCache(url);
    }

    /**
     * send a get request
     */
    public static synchronized boolean sendGet(Context context, HttpParams httpParams,
                                               HttpListener listener) {

        if (!isConnected(context) || httpParams == null) {
            return false;
        }

        if (doCache(httpParams.getCacheTime(), httpParams.getCacheKey(), listener)){
            return true;
        }

        String url = httpParams.getUrl();
        HttpResponse response = new HttpResponse(httpParams.getCacheTime(), httpParams.getCacheKey(), url, listener);
        Map<String, String> map = httpParams.getParams();
        final Map<String, String> headers = HttpParams.getHeader();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url).append("?");
        for (String key : map.keySet()) {

            stringBuilder.append(key).append("=").append(map.get(key)).append("&");
        }
        url = stringBuilder.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response.stringListener, response.errorListener){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return headers == null ? super.getHeaders() : headers;
            }
        };

        response.setRequest(stringRequest);

        String tag = httpParams.getTag();
        if (tag == null || tag.length() < 1) {

            VolleyUtils.addRequest(stringRequest, context);
            ILog.e("===TAG is null===", "http will not be canceled even if activity or fragment stop");
        } else {
            VolleyUtils.addRequest(stringRequest, httpParams.getTag(), context);
        }
        ILog.i("===INetWork url===", httpParams.getUrl());
        ILog.i("===INetWork params===", httpParams.getParams().toString());
        return true;
    }

    /**
     * send a post request
     */
    public static synchronized boolean sendPost(Context context, HttpParams httpParams,
                                                HttpListener listener) {


        if (!isConnected(context) || httpParams == null) {
            return false;
        }

        if (doCache(httpParams.getCacheTime(), httpParams.getCacheKey(), listener)){
            return true;
        }

        String url = httpParams.getUrl();
        final Map<String, String> params = httpParams.getParams();
        final Map<String, String> headers = HttpParams.getHeader();
        HttpResponse response = new HttpResponse(httpParams.getCacheTime(), httpParams.getCacheKey(), url, listener);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response.stringListener, response.errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return headers == null ? super.getHeaders() : headers;
            }
        };

        response.setRequest(stringRequest);

        String tag = httpParams.getTag();
        if (tag == null || tag.length() < 1) {

            VolleyUtils.addRequest(stringRequest, context);
            ILog.e("===TAG is null===", "http will not be canceled even if activity or fragment stop");
        } else {
            VolleyUtils.addRequest(stringRequest, httpParams.getTag(), context);
        }

        ILog.i("===INetWork url===", httpParams.getUrl());
        ILog.i("===INetWork params===", httpParams.getParams().toString());
        return true;
    }

    public static synchronized boolean postJson(Context context, final HttpParams httpParams
            , HttpListener listener) {

        if (!isConnected(context) || httpParams == null) {
            return false;
        }

        if (doCache(httpParams.getCacheTime(), httpParams.getCacheKey(), listener)){
            return true;
        }

        String url = httpParams.getUrl();
        final Map<String, String> headers = HttpParams.getHeader();
        HttpResponse response = new HttpResponse(httpParams.getCacheTime(), httpParams.getCacheKey(), url, listener);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, httpParams.getJson(),
                response.jsonListener, response.errorListener){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return headers == null ? super.getHeaders() : headers;
            }
        };

        response.setRequest(jsonObjectRequest);

        String tag = httpParams.getTag();
        if (tag == null || tag.length() < 1) {

            VolleyUtils.addRequest(jsonObjectRequest, context);
            ILog.e("===TAG is null===", "http will not be canceled even if activity or fragment stop");
        } else {
            VolleyUtils.addRequest(jsonObjectRequest, httpParams.getTag(), context);
        }
        ILog.i("===INetWork url===", httpParams.getUrl());
        ILog.i("===INetWork params===", httpParams.getParams().toString());
        return true;
    }

    /**
     * 上传一组文件
     * files    文件
     * @return  true if success or return false
     */
    public static boolean uploadFile(FormFile[] files, Context context, HttpParams httpParams, ProcessListener listener){

        if (!isConnected(context) || httpParams == null) {
            return false;
        }

        MultipartRequest request = new MultipartRequest(httpParams.getUrl(),listener, httpParams.getParams(), files);
        request.setRetryPolicy(new DefaultRetryPolicy(
                90 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        String tag = httpParams.getTag();
        if (tag == null || tag.length() < 1) {

            VolleyUtils.addRequest(request, context);
            ILog.e("===TAG is null===", "http will not be canceled even if activity or fragment stop");
        } else {
            VolleyUtils.addRequest(request, httpParams.getTag(), context);
        }
        return true;
    }

    /**
     * whether is connected to network .
     *
     * @return true if connect or else return false
     */
    public static boolean isConnected(Context context) {

        if (context == null){
            return false;
        }

        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        boolean value =  networkInfo != null && networkInfo.isAvailable();
        if (!value && showToast){
            ToastUtil.showTop(R.string.error_network, context);
        }
        return value;
    }

    /**
     * 检测cache里是否有这个请求
     */
    private static boolean doCache(int cacheTime, String url, HttpListener listener){

        String tmp = HttpResponse.getCache(cacheTime, url);
        if (tmp == null){
            return false;
        }
        listener.success(tmp, url);
        return true;
    }

    /**
     * whether is wifi
     *
     * @param context
     * @return true if is wifi or return false
     */

    public static boolean isWifi(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }
}