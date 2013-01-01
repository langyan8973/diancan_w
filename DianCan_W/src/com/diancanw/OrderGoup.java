package com.diancanw;

import com.httpw.HttpDownloader;
import com.modelw.Order;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class OrderGoup extends ActivityGroup {

	public LinearLayout rootLayout;
	public LocalActivityManager activityManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordergroup);
		
		rootLayout=(LinearLayout)findViewById(R.id.group_Layout);
		rootLayout.removeAllViews();
		activityManager = getLocalActivityManager();
		//启用图片缓存
		HttpDownloader.enableHttpResponseCache();
		
		Intent intent1=getIntent();
		Order o=(Order)intent1.getSerializableExtra("order");
		
		if(o==null)
		{
			Intent intent=new Intent(OrderGoup .this,OrderList.class);
	        Window subActivity=getLocalActivityManager().startActivity("OrderList",intent);
	        View view=subActivity.getDecorView();
	        rootLayout.addView(view);  
	        LayoutParams params=(LayoutParams) view.getLayoutParams();
	        params.width=LayoutParams.FILL_PARENT;
	        params.height=LayoutParams.FILL_PARENT;
	        view.setLayoutParams(params);
		}
		else{
			Intent intent=new Intent(OrderGoup .this,OrderPage.class);
			intent.putExtra("order", o);
	        Window subActivity=getLocalActivityManager().startActivity("OrderPage",intent);
	        View view=subActivity.getDecorView();
	        rootLayout.addView(view);  
	        LayoutParams params=(LayoutParams) view.getLayoutParams();
	        params.width=LayoutParams.FILL_PARENT;
	        params.height=LayoutParams.FILL_PARENT;
	        view.setLayoutParams(params);
		}
		
	}

	/***
	 * 转到订单列表页面
	 */
	public void ToOrderList()
	{
		Activity activity=activityManager.getCurrentActivity();
		Window w1=activity.getWindow();
		View v1=w1.getDecorView();
		Animation sAnimation=AnimationUtils.loadAnimation(OrderGoup.this, R.anim.close_out);
		v1.startAnimation(sAnimation);
		rootLayout.removeAllViews();				
		
		Intent intent=new Intent(OrderGoup .this,OrderList.class);
        Window subActivity=getLocalActivityManager().startActivity("OrderList",intent);
        View view=subActivity.getDecorView();
        rootLayout.addView(view);  
        LayoutParams params=(LayoutParams) view.getLayoutParams();
        params.width=LayoutParams.FILL_PARENT;
        params.height=LayoutParams.FILL_PARENT;
        view.setLayoutParams(params);
		return;
		
	}
	/***
	 * 监听返回按键
	 */
    @Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
    	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { 
	        if (event.getAction() == KeyEvent.ACTION_DOWN 
	                && event.getRepeatCount() == 0) { 
	        	String strid=activityManager.getCurrentId();
	    		if(strid==null)
	    		{
	    			return super.dispatchKeyEvent(event);
	    		}
	    		else if(strid.equals("OrderPage"))
	    		{
	    			ToOrderList();
	    			return true;
	    		}
	    		else {
	    			return super.dispatchKeyEvent(event);
				}
	        } 
	    } 
		return super.dispatchKeyEvent(event);

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
    	return this.getCurrentActivity().onTouchEvent(event);
	}
	
}
