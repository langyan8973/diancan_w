package com.diancanw.declare;

import java.util.ArrayList;
import java.util.HashMap;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.diancanw.InitPage;
import com.diancanw.R;
import com.diancanw.model.LoginResponse;
import com.diancanw.model.ServiceMess;

import android.app.Application;
import android.app.Notification;

public class DiancanwApp extends Application {

	public boolean isMessPage=false;
	public ArrayList<ServiceMess> messArrayList=new ArrayList<ServiceMess>();
	public LoginResponse loginResponse;
	public String udidString;
	public HashMap<String, String> hashTypes;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);   
	}
}
