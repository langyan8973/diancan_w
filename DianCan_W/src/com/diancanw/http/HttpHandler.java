package com.diancanw.http;

import android.R.integer;
import android.os.Handler;
import android.os.Message;

public class HttpHandler extends Handler {
	public static final int REQUEST_ERROR=0;
	public static final int REQUEST_DESKTYPES=1;
	public static final int REQUEST_DESKS=2;
	public static final int REQUEST_DESK=3;
	public static final int REQUEST_ORDER=4;
	public static final int REQUEST_ORDERS=5;
	public static final int REQUEST_ORDERS_ORDER=6;
	public static final int REQUEST_ORDER_REFRESH=7;
	public static final int REQUEST_ORDER_CHECK=8;
	public static final int REQUEST_ORDER_CANCEL=9;
	public static final int REQUEST_ORDER_ITEMCHANGE=10;
	public static final int REQUEST_RECIPE_CATEGORY=11;
	
	HttpRequestCallback mCallback;
	
	public HttpHandler(HttpRequestCallback callback){
		mCallback=callback;
	}
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what==HttpHandler.REQUEST_ERROR){
			mCallback.RequestError(msg.obj.toString());
		}
		else{
			mCallback.RequestComplete(msg);
		}
	}

}
