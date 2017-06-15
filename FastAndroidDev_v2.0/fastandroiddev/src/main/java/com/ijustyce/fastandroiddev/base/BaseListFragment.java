package com.ijustyce.fastandroiddev.base;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.DateUtil;
import com.ijustyce.fastandroiddev.baseLib.utils.IJson;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;
import com.ijustyce.fastandroiddev.net.IResponseData;
import com.macjay.pulltorefresh.PullToRefreshBase;
import com.macjay.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yc on 2015/12/11 0011.    列表Fragment的父类
 */
public abstract class BaseListFragment<T> extends BaseFragment {

    public PullToRefreshListView mPullListView;
    public LinearLayout noData;

    private static final String FORMATTER = "yyyy-MM-dd HH:mm";

    public Handler handler;
    public BaseAdapter adapter;
    public List<T> data;

    public int pageNo = 1;
    public static final int SHORT_DELAY = 100; // 刷新间隔
    private LinearLayout header;

    @Override
    final void doInit() {

        init();
    }

    public final void refresh() {

        doResume();
    }

    public boolean showNoData(){
        return true;
    }

    public View getHeaderView(){return null;}
    public View getFooterView(){return null;}

    /**
     * 添加header ，不是向listView添加，也不会滚动
     * @param child headerView
     */
    public final void addHeader(View child){

        if (header == null){
            header = (LinearLayout)mView.findViewById(R.id.header);
        }if (header != null && child != null) {
            header.setVisibility(View.VISIBLE);
            header.addView(child);
        }
    }

    @Override
    public void doResume() {

        //  刷新数据
        if (mPullListView != null) {
            mPullListView.doPullRefreshing(true, SHORT_DELAY);
        }
    }

    public abstract Class getType();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_list_common;
    }

    @Override
    public final void onSuccess(String object, String taskId) {
        if (data == null) {
            handler.post(hasNoData);
            return;
        }
        if (pageNo == 1){
            data.clear();
        }

        Object result = IJson.fromJson(object, getType());
        if (result instanceof IResponseData){

            List<T> objectsList = ((IResponseData<T>)result).getData();
            if (objectsList != null && !objectsList.isEmpty()){
                data.addAll(objectsList);
                handler.post(newData);
            }else{
                handler.post(hasNoData);
            }
        }
    }

    public final T getById(int position){

        if (position < 0 || position >= data.size()){
            return null;
        }
        return data.get(position);
    }

    @Override
    public void onFailed(int code, String msg, String taskId) {

        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        handler.post(hasNoData);
    }

    private void init() {

        mPullListView = (PullToRefreshListView) mView.findViewById(R.id.list);
        noData = (LinearLayout) mView.findViewById(R.id.noData);

        if (noData != null){
            noData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPullListView.doPullRefreshing(true, SHORT_DELAY);
                }
            });
        }

        handler = new Handler();
        mPullListView.setPullLoadEnabled(true);
        mPullListView.setPullRefreshEnabled(true);
        mPullListView.setOnRefreshListener(refreshListener);
        ListView lv = mPullListView.getRefreshableView();
        lv.setDivider(null);
        data = new ArrayList<>();
        adapter = buildAdapter(mContext, data);
        if(adapter == null){
            ILog.e("===BaseListFragment===", "adapter can not be null ...");
        }
        if (getHeaderView() != null) {
            lv.addHeaderView(getHeaderView());
        }if (getFooterView() != null) {
            lv.addFooterView(getFooterView());
        }
        lv.setAdapter(adapter);
    }

    private PullToRefreshBase.OnRefreshListener<ListView> refreshListener =
            new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                    if (noData == null) {  //  发生异常
                        handler.post(hasNoData);
                        return;
                    }
                    noData.setVisibility(View.GONE);  //  隐藏没有数据时，显示的view

                    pageNo = 1;
                    mPullListView.setHasMoreData(true);
                    mPullListView.setPullLoadEnabled(true);
                    mPullListView.setPullRefreshEnabled(true);
                    if (!getMoreData()) {
                        handler.post(hasNoData);
                    }
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                    pageNo++;
                    getMoreData();
                }
            };

    //  获取更多数据
    public abstract boolean getMoreData();

    public abstract BaseAdapter buildAdapter(Context mContext, List<T> data);

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mContext != null) {
            mContext = null;
        }
        if (adapter != null) {
            adapter = null;
        }
        if (handler != null) {
            handler = null;
        }
        if (data != null) {
            data = null;
        }
    }

    public final Runnable newData = new Runnable() {
        @Override
        public void run() {

            mPullListView.onPullDownRefreshComplete();
            mPullListView.onPullUpRefreshComplete();
            mPullListView.setLastUpdatedLabel(DateUtil.getDateString(FORMATTER));
            if (mContext != null && adapter != null && data != null) {
                adapter.notifyDataSetChanged();
            }

            if ((data == null || !data.isEmpty()) && noData != null) {
                noData.setVisibility(View.GONE);
            }
        }
    };

    public final Runnable hasNoData = new Runnable() {
        @Override
        public void run() {
            mPullListView.onPullDownRefreshComplete();
            mPullListView.onPullUpRefreshComplete();

            mPullListView.setPullLoadEnabled(false);
            mPullListView.setLastUpdatedLabel(DateUtil.getDateString(FORMATTER));

            if (adapter != null){
                adapter.notifyDataSetChanged();
            }

            if (showNoData() && data != null && data.isEmpty() && noData != null) {
                noData.setVisibility(View.VISIBLE);
            }
        }
    };
}
