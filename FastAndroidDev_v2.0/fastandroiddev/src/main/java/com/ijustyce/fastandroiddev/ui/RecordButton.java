package com.ijustyce.fastandroiddev.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ijustyce.fastandroiddev.R;

import java.io.File;
import java.io.IOException;

public class RecordButton extends Button {

    private String mFileName, tmp, hint;

    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 2000, DELAY = 1000;// 2s
    private long startTime;
    private int time, tmpTime;
    private Handler handler;

    /**
     * 取消语音发送
     */
    private Dialog recordIndicator;

    private static int[] res = {R.mipmap.mic_1, R.mipmap.mic_2, R.mipmap.mic_3,
            R.mipmap.mic_4, R.mipmap.mic_5};

    private static ImageView view;
    private static TextView textView;
    private MediaRecorder recorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSavePath(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        tmp = path;
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    /**
     * 设置录音的最大长度 单位 秒
     * @param minutes 秒
     */
    public void setMaxTime(int minutes){

        time = minutes;
    }

    private Runnable autoFinish = new Runnable() {
        @Override
        public void run() {
            finishRecord();
        }
    };

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        handler = new Handler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                		setText("松开发送");
                initDialogAndStartRecord();
                break;
            case MotionEvent.ACTION_UP:
                //	this.setText("按住录音");
                finishRecord();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelRecord();
                Toast.makeText(getContext(), "cancel", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    private void initDialogAndStartRecord() {

        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),
                R.style.like_toast_dialog_style);
        hint = getContext().getResources().getString(R.string.record_hint);
        View recordBt = LayoutInflater.from(getContext()).inflate(R.layout.record_button, null);
        view = (ImageView) recordBt.findViewById(R.id.imageView);
        textView = (TextView) recordBt.findViewById(R.id.textView);
        recordIndicator.setContentView(recordBt, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recordIndicator.setOnDismissListener(onDismiss);
        LayoutParams lp = recordIndicator.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        startRecording();
        recordIndicator.show();
    }

    private void finishRecord() {

        //  可能已经auto finish 了，所以不予处理
        if (recorder == null){
            return;
        }
        stopRecording();
        recordIndicator.dismiss();

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
            File file = new File(mFileName);
            file.delete();
            return;
        }

        if (finishedListener != null) {
            finishedListener.onFinishedRecord(mFileName, (int) (intervalTime / 1000));
        }
    }

    private void cancelRecord() {

        stopRecording();
        recordIndicator.dismiss();

        Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
        File file = new File(mFileName);
        file.delete();
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            if (tmpTime > 0 && textView != null){
                textView.setText(hint.replace("#num#", "" + tmpTime));
                tmpTime--;
                handler.postDelayed(updateTime, DELAY);
            }else{
                handler.removeCallbacksAndMessages(null);
                handler.post(autoFinish);
            }
        }
    };

    private void startRecording() {

        mFileName = tmp + System.currentTimeMillis() + ".amr";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setAudioChannels(1);
        recorder.setAudioEncodingBitRate(4000);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recorder.setOutputFile(mFileName);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  开始倒计时
        if (handler == null){
            handler = new Handler();
        }if (time > 0) {
            tmpTime = time;
            handler.postDelayed(autoFinish, time * 1000);
            handler.post(updateTime);
        }

        recorder.start();
        thread = new ObtainDecibelThread();
        thread.start();

    }

    private void stopRecording() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            try {
                recorder.stop();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
            recorder.release();
            recorder = null;
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (f < 26) {
                        volumeHandler.sendEmptyMessage(1);
                    }
                    else if (f < 32) {
                        volumeHandler.sendEmptyMessage(2);
                    }
                    else if (f < 38) {
                        volumeHandler.sendEmptyMessage(3);
                    }
                    else {
                        volumeHandler.sendEmptyMessage(4);
                    }
                }else{
                    volumeHandler.sendEmptyMessage(0);
                }

            }
        }

    }

    private OnDismissListener onDismiss = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    static class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            view.setImageResource(res[msg.what]);
        }
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(String audioPath, int time);
    }
}