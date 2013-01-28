package com.diancanw;

import java.io.File;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.model.LoginResponse;
import com.diancanw.utils.DisplayUtil;
import com.diancanw.utils.FileUtils;
import com.diancanw.utils.MenuUtils;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;

public class InitPage extends Activity {
	int sWidth;
	int sHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.initpage);
        Init();
	   
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		DiancanwApp declare_w=(DiancanwApp)InitPage.this.getApplicationContext();
        String udid =  JPushInterface.getUdid(getApplicationContext());
        declare_w.udidString=udid;
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //判断用户信息是否存在
        SharedPreferences userInfo = getSharedPreferences("user_info", 0);
        String token = userInfo.getString("token", "");
        int restaurantid = userInfo.getInt("restaurantid", 0);
        int waiterid=userInfo.getInt("waiterid", 0);
        if(TextUtils.isEmpty(token)){
        	RegisterUdid(udid);
        	Intent intent=new Intent(this,LoginPage.class);
	        startActivity(intent);
	        this.finish();
        	return;
        }
        
        //读取用户信息
        LoginResponse loginResponse=new LoginResponse();
        loginResponse.setRestaurantid(restaurantid);
        loginResponse.setWaiterid(waiterid);
        loginResponse.setToken(token);
        declare_w.loginResponse=loginResponse;
        
		Intent intent=new Intent(this,main.class);
        startActivity(intent);
        this.finish();
	}

	/***
	 * 初始化
	 */
	public void Init() {
		//屏幕尺寸容器
  		DisplayMetrics dm;
  		dm = new DisplayMetrics();
  		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
  		DisplayUtil.SCALE=dm.density;
  		DisplayUtil.DPWIDTH=DisplayUtil.px2dip(dm.widthPixels);
  		DisplayUtil.DPHEIGHT=DisplayUtil.px2dip(dm.heightPixels)-25;
  		System.out.println("屏幕密度："+dm.density);
  		System.out.println("dp宽度："+DisplayUtil.DPWIDTH);
  		System.out.println("dp高度："+DisplayUtil.DPHEIGHT);
        
        sWidth = DisplayUtil.dip2px(DisplayUtil.DPWIDTH);
		sHeight=DisplayUtil.dip2px(DisplayUtil.DPHEIGHT-108);
		String filepathString=Environment.getExternalStorageDirectory().getPath()+"/ChiHuoPro/MenuImg/";
		FileUtils.cacheDir  = new File(filepathString);
		
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
		MenuUtils.initUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+getResources().getString(R.string.image_service);
        //启用图片缓存
      	HttpDownloader.enableHttpResponseCache();
		// 设置通知样式
      	BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(InitPage.this);
      	builder.statusBarDrawable = R.drawable.notification_icon;
      	builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
      	builder.notificationDefaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;  // 设置为铃声与震动都要
      	JPushInterface.setPushNotificationBuilder(1, builder);
      	
	    
	}
	
	private void RegisterUdid(final String udid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpDownloader.RegisterUdid(udid,MenuUtils.initUrl+ "device");
					
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	

}
