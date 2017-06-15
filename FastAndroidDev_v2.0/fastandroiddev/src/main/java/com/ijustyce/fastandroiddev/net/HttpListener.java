package com.ijustyce.fastandroiddev.net;

/**
 * Created by yc on 2015/8/14.
 */
public interface HttpListener {

    void success(String object, String taskId);
    void fail(int code, String msg, String taskId);
}
