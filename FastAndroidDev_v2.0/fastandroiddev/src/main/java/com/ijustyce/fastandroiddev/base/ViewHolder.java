package com.ijustyce.fastandroiddev.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijustyce.fastandroiddev.R;
import com.ijustyce.fastandroiddev.baseLib.utils.CommonTool;
import com.ijustyce.fastandroiddev.baseLib.utils.ILog;

/**
 * Created by yc on 15-12-23.
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Context mContext;
    private int mLayoutId;

    private static final int KEY = R.string.ViewHolder;  //  随便写，但必须是资源值

    public ViewHolder(Context context, ViewGroup parent, int layoutId,
                      int position) {
        mContext = context;
        mLayoutId = layoutId;
        this.mPosition = position;
        this.mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        mConvertView.setTag(KEY, this);
    }

    public ViewHolder(Context context, View view){

        this.mContext = context;
        this.mConvertView = view;
        this.mViews = new SparseArray<>();
    }

    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = null;
            Object object = convertView.getTag(KEY);
            if (object instanceof ViewHolder){
                holder = (ViewHolder) object;
                holder.mPosition = position;
            }else{
                ILog.e("===ViewHolder===", "holder is null, maybe you call " +
                        "setTag(R.string.ViewHolder, ***) in somewhere ...");
                return new ViewHolder(context, parent, layoutId, position);
            }
            return holder;
        }
    }

    public void setPosition(int mPosition){

        this.mPosition = mPosition;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        if (view == null){
            ILog.e("===view is null, id is " + viewId);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setText(int viewId, int resId) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(mContext.getResources().getString(resId));
        }
        return this;
    }
    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setText(int viewId, int resId, String oldChar, Object newChar) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(mContext.getResources().getString(resId).replace(oldChar, "" + newChar));
        }
        return this;
    }
    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(text == null ? "" : text);
        }
        return this;
    }

    /**
     * 设置 View的 tag 值
     *
     * @param viewId
     * @param tag
     * @return
     */
    public ViewHolder setTag(int viewId, String tag) {
        View tv = getView(viewId);
        if (tv != null) {
            tv.setTag(tag);
        }
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageResource(resId);
        }
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, int resId) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageBitmap(CommonTool.drawableToBitmap
                    (mContext.getResources().getDrawable(resId)));
        }
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundResource(backgroundRes);
        }
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(mContext.getResources().getColor(textColorRes));
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public ViewHolder setInvisible(int viewId, boolean invisible) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(invisible ? View.INVISIBLE : View.VISIBLE);
        }
        return this;
    }

    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        if (view != null) {
            view.setChecked(checked);
        }
        return this;
    }

    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId,
                                             View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }

}
