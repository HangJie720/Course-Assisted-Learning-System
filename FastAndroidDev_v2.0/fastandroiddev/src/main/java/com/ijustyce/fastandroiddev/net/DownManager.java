package com.ijustyce.fastandroiddev.net;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.ijustyce.fastandroiddev.baseLib.utils.ILog;
import com.ijustyce.fastandroiddev.baseLib.utils.RegularUtils;

import java.io.File;

/**
 * Created by yangchun on 16/4/5.   文件下载
 */
public class DownManager {

    private static DownloadManager downloadManager;
    private long myDownloadReference;
    private DownloadManager.Request request;
    private Context context;
    private downloadListener listener;

    public DownManager(Context context, String fileUrl, downloadListener listener) {

        this.context = context;
        doInit();

        if (!RegularUtils.isUrl(fileUrl)) {

            ILog.e("===DownManager===", "url is not right ...");
            return;
        }

        Uri uri = Uri.parse(fileUrl);
        request = new DownloadManager.Request(uri);
        this.listener = listener;
    }

    private void doInit() {

        if (context == null || downloadManager != null) {
            return;
        }

        String serviceString = Context.DOWNLOAD_SERVICE;
        downloadManager = (DownloadManager) context.getSystemService(serviceString);
    }

    public void setOnlyWifi() {

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
    }

    public void setTitle(String title) {

        request.setTitle(title);
    }

    public void setDesc(String desc) {

        request.setDescription(desc);
    }

    @TargetApi(11)
    public void hideNotify() {

        request.setNotificationVisibility(View.GONE);
    }

    public void hideInSystemDownUi() {

        request.setVisibleInDownloadsUi(false);
    }

    public void setSaveFilePath(File f){

        request.setDestinationUri(Uri.fromFile(f));
    }

    public void setDownInPublicDir(String fileName){

        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, fileName);
    }

    public DownloadManager.Request getRequest(){

        return request;
    }

    public void startDown() {

        if (context == null || downloadManager == null) {
            return;
        }

        myDownloadReference = downloadManager.enqueue(request);

        context.registerReceiver(receiver, filter);
    //    context.registerReceiver(clickReceiver, clickFilter);
    }

    //  如果还在下载，则停止，并且，无论文件是否下载完毕，都会删除，之后，这个request将不可用！可用于自动更新后删除文件
    public void delete(){

        downloadManager.remove(myDownloadReference);
    }

    public void unregister(){

        if (context != null) {
            try {
                context.unregisterReceiver(receiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public interface downloadListener{

        public void onDownloadFinish(String filePath, String fileUri);
    }

    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (myDownloadReference == reference) {

                if (listener == null || downloadManager == null){
                    unregister();
                    return;
                }

                DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
                myDownloadQuery.setFilterById(reference);

                Cursor myDownload = downloadManager.query(myDownloadQuery);
                if (myDownload.moveToFirst()) {
                    int fileNameIdx =
                            myDownload.getColumnIndex("local_filename");
                    int fileUriIdx =
                            myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);

                    String fileName = myDownload.getString(fileNameIdx);
                    String fileUri = myDownload.getString(fileUriIdx);
                    listener.onDownloadFinish(fileName, fileUri);
                }
                myDownload.close();
                unregister();
            }
        }
    };

    IntentFilter clickFilter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
    BroadcastReceiver clickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraID = "extra_click_download_ids";
            long[] references = intent.getLongArrayExtra(extraID);
            for (long reference : references)
                if (reference == myDownloadReference) {

                    //  TODO clicked
                }
        }
    };

}
