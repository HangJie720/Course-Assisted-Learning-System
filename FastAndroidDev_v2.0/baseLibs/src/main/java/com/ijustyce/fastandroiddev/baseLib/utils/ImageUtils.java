package com.ijustyce.fastandroiddev.baseLib.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * Created by yc on 15-12-24.   再次封装的ImageLoader 新增图片预览以及仅wifi加载图片功能
 */

public class ImageUtils {

    private static boolean onlyWiFi = false;  //  是否加载网络图片
    private static Intent mIntent;  //  点击图片启动的class
    private static ImageLoader imageLoader = ImageLoader.getInstance();

    private static boolean viewEnabled = true;

    private static ImageLoaderConfiguration config;
    public static DisplayImageOptions defaultOption;

    static {

        defaultOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
    }

    public static void setViewEnabled(boolean value) {

        viewEnabled = value;
    }

    public static boolean isViewEnabled() {

        return viewEnabled;
    }

    public static void init(Context context) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        config = new ImageLoaderConfiguration
                .Builder(context)
                .defaultDisplayImageOptions(defaultOption)
                .memoryCacheExtraOptions(dm.widthPixels, dm.heightPixels)
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 设置点击图片启动的Intent，但如果展示图片时context为null，则不生效
     *
     * @param intent
     */
    public static void setViewIntent(Intent intent) {

        setViewEnabled(true);
        ImageUtils.mIntent = intent;
    }

    /**
     * 设置是否非wifi下加载网络图片
     *
     * @param value true 为加载, false不加载, 默认加载
     */
    public static void loadOnlyWifi(boolean value) {

        onlyWiFi = value;
    }

    private static boolean isCached(String url) {

        return DiskCacheUtils.findInCache(url, imageLoader.getDiskCache()) == null
                || MemoryCacheUtils.findCacheKeysForImageUri(url,
                imageLoader.getMemoryCache()).isEmpty();
    }

    public static void load(final Context context, ImageView view, final String url,
                            final boolean clickToView) {

        if (config == null || !imageLoader.isInited()) {

            init(context);
        }

        if (view == null) {

            ILog.e("view is null, cancel display image ... ");
            return;
        }

        if (StringUtils.isEmpty(url)) {

            ILog.e("url is null, cancel display image ... ");
            view.setImageDrawable(view.getDrawable());
            return;
        }

        if (onlyWiFi && !CommonTool.isWifi(context) && RegularUtils.isUrl(url) && !isCached(url)) {

            ILog.i("do not display network picture , return ");
            view.setImageDrawable(view.getDrawable());
            return;
        }

        view.setImageBitmap(null);
        imageLoader.displayImage(url, view);

        //  如果context 不为空则添加图片浏览功能
        if (context == null || url.startsWith("drawable://")) {
            return;
        }

        if (!clickToView) {
            return;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  图片预览功能
                if (viewEnabled && mIntent != null) {
                    context.startActivity(mIntent.putExtra("url", url)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }

    /**
     * 加载图片
     *
     * @param context 如果为空，则点击图片将不启动浏览activity
     * @param view    imageView ，已判断空指针
     * @param url     图片链接
     */
    public static void load(final Context context, ImageView view, final String url) {

        load(context, view, url, false);
    }
}
