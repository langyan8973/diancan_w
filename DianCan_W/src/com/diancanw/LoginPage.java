package com.diancanw;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

import com.declarew.Declare_w;
import com.httpw.HttpDownloader;
import com.modelw.LoginResponse;
import com.modelw.restaurant;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Activity {

	EditText m_CodeEditText;
	EditText m_UserNameEditText;
	EditText m_PasswordEditText;
	Button   m_LoginButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.loginpage);
		
		m_CodeEditText=(EditText)findViewById(R.id.restaurantCode);
		m_UserNameEditText=(EditText)findViewById(R.id.userName);
		m_PasswordEditText=(EditText)findViewById(R.id.passwordText);
		m_LoginButton=(Button)findViewById(R.id.loginBtn);
		m_LoginButton.setOnClickListener(new LoginClick());
	}
	
	/**
	 * 把用户信息写入文件
	 * @param user
	 */
	private void WriteLoginResponse(final String strLoginResponse){
		
		new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub				
					try {
						SaveLoginResponse(strLoginResponse);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}).start();
		
	}
	
	/**
	 * 存储用户信息
	 * @param str
	 * @throws IOException
	 */
	public void SaveLoginResponse(String str) throws IOException
	{
		this.deleteFile("Login.txt");
		FileOutputStream outputStream=this.openFileOutput("Login.txt", Context.MODE_APPEND);
        outputStream.write(str.getBytes());  
        outputStream.close();
	}

	/**
	 * 显示错误信息
	 * @param str
	 */
	private void ShowToast(String str){
		Toast toast = Toast.makeText(LoginPage.this, str, Toast.LENGTH_SHORT); 
        toast.show();
	}
	
	
	/***
	 * 登录按钮点击
	 */
	class LoginClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String codeString=m_CodeEditText.getText().toString();
			String nameString=m_UserNameEditText.getText().toString();
			String passString=m_PasswordEditText.getText().toString();
			Declare_w declare_w=(Declare_w)LoginPage.this.getApplicationContext();
			String udidString=declare_w.udidString;
			if(codeString==""||nameString==""||passString==""){
				ShowToast("登录信息填写不全！");
				return;
			}
			
			int code=Integer.parseInt(codeString);
			try {
				ArrayList<String> infoArrayList=MenuUtils.Login(code, nameString, passString,udidString);
				if(infoArrayList==null){
					ShowToast("登录失败！");
					return;
				}
				
				
				LoginResponse loginResponse=new LoginResponse();
				loginResponse.setToken(infoArrayList.get(0).toString());
				String strRestaurant=infoArrayList.get(1).toString();
				restaurant restaurant=JsonUtils.parseJsonToRestaurant(strRestaurant);
				loginResponse.setRestaurantid(restaurant.getId());
				SharedPreferences userInfo = getSharedPreferences("user_info", 0);
				userInfo.edit().putString("token",infoArrayList.get(0).toString()).commit();
			    userInfo.edit().putInt("restaurantid", restaurant.getId()).commit();
				declare_w.loginResponse=loginResponse;
				
				Intent intent=new Intent(LoginPage.this,main.class);
		        startActivity(intent);
		        LoginPage.this.finish();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ShowToast(e.getMessage());
			}
		}
		
	}
	
}
