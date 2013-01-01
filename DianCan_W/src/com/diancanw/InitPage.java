package com.diancanw;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import org.androidpn.clientw.ServiceManager;

import com.declarew.Declare_w;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.modelw.LoginResponse;
import com.utilsw.DisplayUtil;
import com.utilsw.FileUtils;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
        //判断用户文件是否存在
        File userFile= new File(this.getApplicationContext().getFilesDir()+"/Login.txt");
        if(!userFile.exists()){
        	Intent intent=new Intent(this,LoginPage.class);
	        startActivity(intent);
	        this.finish();
        	return;
        }
        
        //读取用户信息
	    String jsonString="";
	    try {
	    		jsonString=ReadLoginInfo();
	    		Declare_w declare_w=(Declare_w)InitPage.this.getApplicationContext();
				LoginResponse loginResponse=JsonUtils.parseJsonToLoginResponse(jsonString);
				declare_w.loginResponse=loginResponse;
	    		Intent intent=new Intent(this,main.class);
		        startActivity(intent);
		        this.finish();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
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
		FileUtils.cacheDir  = new File("/sdcard/ChiHuoPro/MenuImg/");
        if (!FileUtils.cacheDir.exists()) {
			FileUtils.cacheDir.mkdirs();
		}
		MenuUtils.initUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.updateUrl="http://"+getResources().getString(R.string.url_service);
        MenuUtils.imageUrl="http://"+getResources().getString(R.string.image_service);
		
		// Start the service
	    ServiceManager serviceManager = new ServiceManager(this);
	    serviceManager.setNotificationIcon(R.drawable.notification);
	    serviceManager.startService();
	    
	}
	
	public String ReadLoginInfo() throws IOException
	{
		FileInputStream inputStream=this.openFileInput("Login.txt");
        ByteArrayOutputStream outStream=new ByteArrayOutputStream();  
        byte[] buffer=new byte[1024];  
        int len=0;  
        while ((len=inputStream.read(buffer))!=-1){  
            outStream.write(buffer, 0, len);  
        }  
        outStream.close();  
        byte[] data=outStream.toByteArray();  
        String jsonstr=new String(data);  
        return jsonstr; 
		
	}

}
