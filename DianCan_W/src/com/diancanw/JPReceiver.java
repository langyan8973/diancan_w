package com.diancanw;

import cn.jpush.android.api.JPushInterface;
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
            
        	//打开自定义的Activity
        	Intent i = new Intent(context, ServicePage.class);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	context.startActivity(i);
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}

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
//        if (null != channel) {
//            msgIntent.putExtra(Constants.KEY_CHANNEL, channel);
//        }
//         
//        JSONObject all = new JSONObject();
//        try {
//            all.put(Constants.KEY_TITLE, title);
//            all.put(Constants.KEY_MESSAGE, message);
//            all.put(Constants.KEY_EXTRAS, new JSONObject(extras));
//        } catch (JSONException e) {
//        }
//        msgIntent.putExtra("all", all.toString());
         
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
