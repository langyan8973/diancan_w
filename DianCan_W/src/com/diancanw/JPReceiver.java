package com.diancanw;

import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.model.Notify;
import com.diancanw.model.Order;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

import cn.jpush.android.api.JPushInterface;
import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class JPReceiver extends BroadcastReceiver {

	private static final String TAG = "JPReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	ProcessCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            ProcessOpenNotification(context,bundle);
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	
	/**
	 * 打开通知
	 * @param context
	 * @param bundle
	 */
	private void ProcessOpenNotification(Context context,Bundle bundle){
		String jsonString=bundle.getString(JPushInterface.EXTRA_EXTRA);
		Notify notify=JsonUtils.parseJsonToNotify(jsonString);
		if(notify==null){
			return;
		}
    	GetOrder(notify.getOid(), context);
	}
	
	/**
	 * 根据id获取订单
	 * @param oid
	 * @param context
	 */
	public void GetOrder(int oid,Context context){
		try {
			DiancanwApp declare_w=(DiancanwApp)context.getApplicationContext();
			String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+declare_w.loginResponse.getRestaurantid()+"/orders/"+oid,
					declare_w.loginResponse.getToken(),declare_w.udidString);
			if(resultString==null)
			{
				return;
			}
			Order order=JsonUtils.ParseJsonToOrder(resultString);
			//打开自定义的Activity
	    	Intent i = new Intent(context, OrderActivity.class);
	    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	i.putExtra("order", order);
	    	context.startActivity(i);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析自定义消息
	 * @param context
	 * @param bundle
	 */
	private void ProcessCustomMessage(Context context,Bundle bundle){
		String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if(TextUtils.isEmpty(message)){
        	return;
        }
        Intent msgIntent = new Intent("diancan");
        msgIntent.addCategory(Intent.CATEGORY_DEFAULT);
        msgIntent.putExtra("message", message);
        msgIntent.putExtra("title", title);
         
        context.sendBroadcast(msgIntent);
	}
	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
		}
		return sb.toString();
	}

}
