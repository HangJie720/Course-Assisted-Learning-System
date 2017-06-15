package com.ijustyce.fastandroiddev.baseLib.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ijustyce.fastandroiddev.baseLib.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * Created by yc on 16-2-6. 通用工具类
 */
public class CommonTool {

    /**
     *  对double 类型数据进行四舍五入，并保留两位小数
     */
    public static double getShortDouble(double value){

        return Math.round(value * 100) / 100.0;
    }

    /**
     * 判断是否是wifi
     * @return 如果是wifi 则返回true，否则返回false
     */

    public static boolean isWifi(@NonNull Context context){

        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        return info!=null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * play notification sound
     *
     * @param context mContext
     * @return soundId
     */
    public static int playNotifi(Context context) {
        NotificationManager mgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification nt = new Notification();
        nt.defaults = Notification.DEFAULT_SOUND;
        int soundId = new Random(System.currentTimeMillis())
                .nextInt(Integer.MAX_VALUE);
        mgr.notify(soundId, nt);
        return soundId;
    }

    public interface PlayFinish {

        public void onFinish();
    }

    /**
     * return VersionName
     *
     * @param context Activity.this
     * @return versionName
     */
    public static String getVersionName(Context context) {

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * return packageName
     *
     * @param context Activity.this
     * @return packageName
     */
    public static String getPkgName(Context context) {

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.applicationInfo.loadLabel(
                    context.getPackageManager()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * return VersionCode
     *
     * @param context Activity.this
     * @return versionCode
     */
    public static int getVersionCode(Context context) {

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @param context Activity.this
     * @return pkgName+" "+versionName , if fail return ""
     */
    public static String getVersion(Context context) {

        return getPkgName(context) + getVersionName(context);
    }

    public static boolean openUrl(String url, Activity mContext){

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        try {
            mContext.startActivity( Intent.createChooser(intent, "请选择应用"));
        } catch (android.content.ActivityNotFoundException ex) {
            return false;
        }
        return true;
    }

    public static void setEditAble(boolean editAble, EditText editText){

        if (editText == null){
            return;
        }
        editText.setFocusableInTouchMode(editAble);
        editText.setLongClickable(editAble);
        if (editAble){
            editText.requestFocus();
        }else {
            editText.clearFocus();
        }
    }

    public static boolean chooseFile(Activity mContext, String type, int requestCode) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type == null ? "*/*" : type + "/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            mContext.startActivityForResult( Intent.createChooser(intent, "请选择文件"), requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            return false;
        }
        return true;
    }

    /**
     * play incoming call sound
     *
     * @param context mContext
     * @return MediaPlayer , by it you call stop current play
     */
    public static MediaPlayer playSound(Context context) {

        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayer mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(context, alert);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        return mMediaPlayer;
    }

    /**
     * play out call sound
     *
     * @param context mContext
     * @param id      id of sound , usually in raw path
     * @return MediaPlayer , by it you call stop current play
     */
    public static MediaPlayer playSound(Context context, int id) {

        MediaPlayer mMediaPlayer = MediaPlayer.create(context, id);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        return mMediaPlayer;
    }

    private static MediaPlayer mPlayer;

    /**
     * play voice , use to voice chat
     *
     * @param file       file
     * @param playFinish PlayFinish listener , 可以为空
     */
    public static MediaPlayer startPlaying(String file, PlayFinish playFinish) {

        final WeakReference<PlayFinish> playListener = new WeakReference<>(playFinish);
        try {
            if (mPlayer != null) {
                stopPlaying();
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(file);
            mPlayer.prepare();
            mPlayer.start();
            ILog.i("===file===", file);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    ILog.i("===Common Tools play finish===");
                    if (playListener.get() != null) {
                        playListener.get().onFinish();
                    } else {
                        ILog.e("===playListener is null, return===");
                    }
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            stopPlaying();
        }
        return mPlayer;
    }

    /**
     * 获取amr 文件的播放长度，返回秒 4舍5入
     *
     * @param file file
     */
    public static int getAmrTime(String file) {

        int time = -1;
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(file);
            mPlayer.prepare();
            time = mPlayer.getDuration();
            stopPlaying();
        } catch (IOException e) {
            stopPlaying();
        }
        return time / 1000 + (time % 1000 > 500 ? 1 : 0);
    }

    /**
     * stop play voice
     */
    public static void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * is phone connect to network
     *
     * @param context mContext
     * @return true if connect or return false
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * open system share dialog
     *
     * @param context mContext
     * @param text    text to share
     * @return true if success or false
     */
    public static void systemShare(Context context, String text) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        //  intent.putExtra(Intent.EXTRA_SUBJECT, text);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getResources().
                getText(R.string.select_app)));
    }

    /**
     * open system share dialog
     *
     * @param context  mContext
     * @param text     text to share
     * @param filePath file to share, usually is picture
     * @return true if success or false
     */
    public static void systemShare(Context context, String text,
                                   String filePath) {

        File f = new File(filePath);
        if (!f.exists()) {

            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
//		intent.putExtra(Intent.EXTRA_SUBJECT, text);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getResources().
                getText(R.string.select_app)));
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String getUUID(Context context) {

        String res;
        TelephonyManager manage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            res = manage.getDeviceId();
            if (res != null && res.length() > 10) {
                return res;
            }
        } catch (Exception e) {
            ILog.e("error", e.getMessage());
        }
        res = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (res == null || res.length() < 5 || "9774d56d682e549c".equals(res)) {
            res = UUID.randomUUID().toString();
        }
        ;
        return res;
    }

    /**
     * hide soft keyboard
     *
     * @param context Activity
     */
    public static void closeIme(Activity context) {

        View view = context.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * save bitmap to .jpg file
     * @param mBitmap bitmap
     * @param bitName file path , must end with .jpg like /sdcard/jilvinfo/tmp/1.jpg
     */
    public static boolean savBitmapToPng(Bitmap mBitmap,String bitName)  {
        File f = new File(bitName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * save bitmap to .jpg file
     * @param mBitmap bitmap
     * @param bitName file path , must end with .jpg like /sdcard/jilvinfo/tmp/1.jpg
     */
    public static boolean savBitmapToJpg(Bitmap mBitmap,String bitName)  {
        File f = new File(bitName);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param drawable drawable
     * @return bitmap bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 显示或隐藏密码
     * @param showPw    true 则显示，否则隐藏
     * @param view      若为空，则直接return
     */
    public static void showPw(boolean showPw, TextView view){

        if (view == null){
            ILog.e("===CommonTool===", "showPw view can not be null ...");
            return;
        }
        if(showPw){
            //如果选中，显示密码
            view.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            //否则隐藏密码
            view.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return bitmap
     */
    public static Bitmap bitmapToRound(Bitmap bitmap) {

        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    @SuppressWarnings("static-access")
    public static Bitmap compressImageFromFile(String filePath) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * @param filePath
     * @return
     */
    public static Bitmap compressImageFromFile(String filePath, int angle) {


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (angle != 0) {
            // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            m.setRotate(angle); // 旋转angle度
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        }
        return bitmap;
    }

    public static String getRandom(int length) {

        String res = "";
        for (int i = 0; i < length; i++) {
            Random random = new Random();
            res += random.nextInt(10);
        }
        return res;
    }

    /**
     * 获取bitmap的大小，返回kb
     *
     * @param bitmap
     * @return
     */
    public static long getBitmapsize(Bitmap bitmap) {

        long size;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            size = bitmap.getByteCount() / 1024;
        } else {
            size = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }
        ILog.i("===bitmap size is " + size + " ===");
        return size;
    }

    /**
     * 压缩图片，如果其大小小于notComPressSize kb 则不压缩
     *
     * @param image
     * @param notComPressSize
     * @return
     */
    public static Bitmap compressImage(Bitmap image, long notComPressSize) {

        if (getBitmapsize(image) > notComPressSize) {

            compressImage(image);
        }
        return image;
    }

    /**
     * compress image
     *
     * @param image image
     * @return bitmap
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        int options = 100;
        int size;
        do {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            size = baos.toByteArray().length / 1024;
            ILog.i("===size===" + size);
            options -= 10;
        } while (size > 60 && options > 0);  //  60 almost 600kb
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }

    /**
     * 直接呼叫指定的号码(需要<uses-permission android:name="android.permission.CALL_PHONE"/>权限)
     *
     * @param mContext    上下文Context
     * @param phoneNumber 需要呼叫的手机号码
     */
    public static void callPhone(Context mContext, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent call = new Intent(Intent.ACTION_CALL, uri);
        try {
            mContext.startActivity(call);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转至拨号界面
     *
     * @param mContext    上下文Context
     * @param phoneNumber 需要呼叫的手机号码
     */
    public static void toCallPhoneActivity(Context mContext, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent call = new Intent(Intent.ACTION_DIAL, uri);
        mContext.startActivity(call);
    }

    /**
     * 直接调用短信API发送信息(设置监听发送和接收状态)
     *
     * @param strPhone      手机号码
     * @param strMsgContext 短信内容
     */
    public static void sendMessage(final Context mContext, final String strPhone, final String strMsgContext) {

        //处理返回的发送状态
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sendIntent = PendingIntent.getBroadcast(mContext, 0, sentIntent, 0);
        // register the Broadcast Receivers
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "短信发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        //处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent backIntent = PendingIntent.getBroadcast(mContext, 0, deliverIntent, 0);
        mContext.registerReceiver(new BroadcastReceiver() {
                                      @Override
                                      public void onReceive(Context _context, Intent _intent) {
                                          Toast.makeText(mContext, strPhone + "已经成功接收", Toast.LENGTH_SHORT).show();
                                      }
                                  },
                new IntentFilter(DELIVERED_SMS_ACTION)
        );

        //拆分短信内容（手机短信长度限制）
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> msgList = smsManager.divideMessage(strMsgContext);
        for (String text : msgList) {
            smsManager.sendTextMessage(strPhone, null, text, sendIntent, backIntent);
        }
    }

    /**
     * 跳转至发送短信界面(自动设置接收方的号码以及短信内容)
     *
     * @param mContext      context
     * @param strPhone      手机号码
     * @param strMsgContext 短信内容
     */
    public static void toSendMessageActivity(Context mContext, String strPhone, String strMsgContext) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(strPhone)) {
            Uri uri = Uri.parse("smsto:" + strPhone);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.putExtra("sms_body", strMsgContext);
            mContext.startActivity(sendIntent);
        }
    }

    /**
     * get screen width
     * @param context context
     * @return
     */
    public static int getScreenWidth(@NonNull Context context){

        WindowManager vm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return vm.getDefaultDisplay().getWidth();
    }

    /**
     * get screen height
     * @param context context
     * @return
     */
    public static int getScreenHeight(@NonNull Context context){

        WindowManager vm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return vm.getDefaultDisplay().getHeight();
    }

    public static void showNotify(String title, String msg,Intent intent,
                                  Context context, int resSmallIcon){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(resSmallIcon)
                .setContentTitle(title) //标题
                .setContentText(msg)    //正文
                //  .setNumber(3)         //设置信息条数
                //  .setContentInfo("3")      //作用同上，设置信息的条数
                //  .setLargeIcon(resSmallIcon)
                .setDefaults(Notification.DEFAULT_SOUND)//设置声音，此为默认声音
                //    .setVibrate(vT) //设置震动，此震动数组为：long vT[]={300,100,300,100};
                //.setLights(argb, onMs, offMs)
                //   .setOngoing(true)      //true，用户不能手动清除
                .setAutoCancel(true);

        if (intent != null){
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
        }
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1000, mBuilder.build());
    }

    public static String getText(TextView view){

        return view == null ? null : view.getText().toString();
    }
}
