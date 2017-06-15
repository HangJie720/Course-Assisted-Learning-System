/**
 * date:2014-04-21
 * rewrite ToastUtil
 */
package com.ijustyce.fastandroiddev.baseLib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ijustyce.fastandroiddev.baseLib.R;

import java.util.ArrayList;
import java.util.List;

public class ToastUtil {

	private static String PKG;
	private static Context context;
	private static List<String> notShowList;

	static {

		notShowList = new ArrayList<>();
	}

	public static void addNotShowWord(String word){

		if (!notShowList.contains(word)){
			notShowList.add(word);
		}
	}

	private static boolean shouldShow(String text){

		if (StringUtils.isEmpty(text) || notShowList.contains(text)){
			return false;
		}

		return true;
	}

	public static void setPkg(String pkg, Context context){

		ToastUtil.PKG = pkg;
		ToastUtil.context = context;
	}

	private static boolean  isTop(){

		if (context == null){
			return false;
		}
		ActivityManager activityManager = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> appTask = activityManager.getRunningTasks(1);

		if (appTask != null && appTask.size() > 0){
			if (appTask.get(0).topActivity.toString().contains(PKG)){
				return true;
			}
		}
		return false;
	}

	public static void show(int id, Context context) {

		if (context == null){
			return ;
		}

		String text = context.getResources().getString(id);
		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.BOTTOM , 0, 90);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	public static void show(Context context, String text) {

		if (context == null){
			return ;
		}

		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.BOTTOM , 0, 90);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	/**
	 *
	 * @param id
	 * @param context
	 */
	public static void showTop(int id, Context context) {

		if(!isTop()){
			return;
		}

		String text = context.getResources().getString(id);
		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.BOTTOM , 0, 90);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	public static void showTop(Context context, String text) {

		if(!isTop()){
			return;
		}

		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.BOTTOM , 0, 90);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	/**
	 *
	 * @param id
	 * @param yOffset dp , height of ToastUnit
	 * @param context
	 */
	public static void showTop(int id, Context context , int yOffset) {

		if(!isTop()){
			return;
		}

		String text = context.getResources().getString(id);
		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast_top, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.TOP , 0, yOffset);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}

	public static void showTop(Context context, String text , int yOffset) {

		if(!isTop()){
			return;
		}

		if (!shouldShow(text)){
			return;
		}

		LayoutInflater mInflater = LayoutInflater.from(context);
		View toastRoot = mInflater.inflate(R.layout.toast_top, null);
		TextView message = (TextView) toastRoot.findViewById(R.id.message);
		message.setText(text);

		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.TOP , 0, yOffset);
		toastStart.setDuration(Toast.LENGTH_LONG);
		toastStart.setView(toastRoot);
		toastStart.show();
	}
}
