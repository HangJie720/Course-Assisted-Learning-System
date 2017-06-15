package com.ijustyce.fastandroiddev.net;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangchun on 16/3/8.   网络请求 自动解析基类
 */
public abstract class IResponseData<T> implements Serializable {

    public abstract List<T> getData();
}
