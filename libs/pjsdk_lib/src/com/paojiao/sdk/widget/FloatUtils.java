package com.paojiao.sdk.widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.paojiao.sdk.CallbackListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;

/**
 * 悬浮窗工具 针对小米手机
 * @author PC
 *
 */
public class FloatUtils {
	
	
	private HashMap<Activity, MiuiFloatView> floatMap ;
	
	
	private static FloatUtils mFloatUtils;
	
	
	public static FloatUtils getnewInstall(){
		if(mFloatUtils ==null){
			mFloatUtils = new FloatUtils();
		}
		return mFloatUtils;
	}
	
	public FloatUtils() {
		floatMap = new HashMap<Activity, MiuiFloatView>();
	}
	
	public void addMiuiFloatView(Activity activity,MiuiFloatView miuiFloatView){
		floatMap.put(activity, miuiFloatView);
	}
	
	public MiuiFloatView getMiuiFloatView(Activity activity){
		MiuiFloatView miuiFloatView = floatMap.get(activity);
		return miuiFloatView;
	}
	
	public void showMiuiFloatAll(){
		Iterator<Entry<Activity, MiuiFloatView>> iterator = floatMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Activity, MiuiFloatView> entry = iterator.next();
			MiuiFloatView miuiFloatView = entry.getValue();
			Activity activity = entry.getKey();
			if(!activity.isFinishing()){
				Display display = activity.getWindowManager().getDefaultDisplay();
				View view = activity.getWindow().getDecorView();
				miuiFloatView.showAtLocation(view, Gravity.TOP | Gravity.LEFT, 0,
						display.getHeight() / 2);
			}else {
				floatMap.remove(activity);
			}
		}
	}
	
	private void removeFloatViewAll(){
		Iterator<Entry<Activity, MiuiFloatView>> iterator = floatMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Activity, MiuiFloatView> entry = iterator.next();
			MiuiFloatView miuiFloatView = entry.getValue();
			Activity activity = entry.getKey();
			if (miuiFloatView!=null) {
				miuiFloatView.dismiss();
			}
			if(activity==null||activity.isFinishing()){
				floatMap.remove(activity);
			}
		}
	}
	
	public void removeFloatView(Activity activity){
		MiuiFloatView miuiFloatView = floatMap.get(activity);
		if(miuiFloatView!=null){
			miuiFloatView.dismiss();
		}
	}
	
	
	int rawX, rawY, XX, YY;
	public void showMiuiFloat(Activity activity){

		if(activity!=null&&!activity.isFinishing()){
			MiuiFloatView miuiFloatView = floatMap.get(activity);
			if(miuiFloatView!=null){
				new CallbackListener(activity) {
					public void onGetCoordinate(Bundle info) {
						super.onGetCoordinate(info);
						rawX = info.getInt("rawX");
						rawY = info.getInt("rawY");
						XX = info.getInt("x");
						YY = info.getInt("y");
					}
				};
				Display display = activity.getWindowManager().getDefaultDisplay();
				int screenWidth = display.getWidth();
				View view = activity.getWindow().getDecorView();
				if(XX==0){
					miuiFloatView.showAtLocation(view, Gravity.TOP | Gravity.LEFT, 0,
					display.getHeight() / 2);
				}else {
					if (rawX < screenWidth / 2) {
						miuiFloatView.showAtLocation(view, Gravity.TOP | Gravity.LEFT,
								0, rawY - YY);
					} else {
						miuiFloatView.showAtLocation(view, Gravity.TOP | Gravity.LEFT,
								screenWidth - XX / 2, rawY - YY);
					}
					
				}
				
				
			}
		}
		
		
	}

	
	

}
