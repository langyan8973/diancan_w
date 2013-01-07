package com.diancanw;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.declarew.Declare_w;
import com.httpw.HttpDownloader;
import com.modelw.Order;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class main extends TabActivity {
    /** Called when the activity is first created. */
	private TabHost m_tabHost;
	private TabWidget m_tabWidget;
	Resources rsResources;
	BroadcastReceiver receiver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        rsResources=getResources();
        
        m_tabHost = getTabHost();  
        m_tabHost.setup();
        m_tabHost.bringToFront();
        
        addDeskListTab();
        addOrderTab();
        addMyServiceTab();
        
        m_tabHost.setCurrentTab(0);
        m_tabWidget = (TabWidget)findViewById(android.R.id.tabs);
        m_tabWidget.setStripEnabled(false);
        
        //注册一个广播接收器 
        receiver = new BroadcastReceiver() {
      	@Override
          public void onReceive(Context ctx, Intent intent) {
      		if (intent.getAction().equals("toOrderPage")) {
      			m_tabHost.setCurrentTab(1);
//     			   View v=m_tabHost.getTabWidget().getChildAt(1);
//     			   TextView txtcount=(TextView)v.findViewById(R.id.txtcount);
//     			   txtcount.setVisibility(View.VISIBLE);
//     			   txtcount.setText(declare.getTotalCount()+"");
      		}
      	}
      };
      IntentFilter filter = new IntentFilter();
      filter.addAction("toOrderPage");
      filter.addCategory(Intent.CATEGORY_DEFAULT);
      registerReceiver(receiver, filter);
    }
    
    /***
     * 创建标签的view
     * @param imageResourceSelector
     * @param text
     * @return
     */
    private View populateTabItem(int imageResourceSelector, String text) {  
  
        View view = View.inflate(this, R.layout.tab_item, null); 
        ((ImageView) view.findViewById(R.id.tab_item_imageview))  
                .setImageResource(imageResourceSelector);  
        ((TextView) view.findViewById(R.id.tab_item_textview)).setText(text);  
        return view;
    }
    
    /***
     * 增加“餐桌”标签
     */
    public void addDeskListTab(){  
        Intent intent = new Intent();  
        intent.setClass(main.this, TableList.class); 
        View v=populateTabItem(R.drawable.mytable,"餐桌");
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu1").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加我的餐桌标签
     */
    public void addMyTableTab(){  
//        Intent intent = new Intent();  
//        intent.setClass(Main.this, TableGroup.class);  
//        String tabnameString="";
//        Declare declare=(Declare)getApplicationContext();  
//        int count=0;
//        if(declare.curOrder!=null)
//        {
//        	if(declare.curOrder.getStatus()==4||declare.curOrder.getStatus()==5)
//        	{
//        		tabnameString=rsResources.getString(R.string.title_Tab2);
//        		count=0;
//        		declare.curOrder=null;
//        	}
//        	else {
//        		tabnameString=declare.curOrder.getDesk().getName();
//            	count=declare.getTotalCount();
//			}
//        	
//        }
//        else {
//        	tabnameString=rsResources.getString(R.string.title_Tab2);
//		}
//        View v=CreateTableTab(R.drawable.mytable, tabnameString,count);
//	    m_tabHost.addTab(m_tabHost.newTabSpec("menu2").setIndicator(v).setContent(intent));
    } 
    
    /***
     * 增加服务标签
     */
    public void addMyServiceTab(){  
        Intent intent = new Intent();  
        intent.setClass(main.this, ServicePage.class);     
        View v=populateTabItem(R.drawable.myservice, "服务");
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu3").setIndicator(v).setContent(intent));
    }  
    
    /***
     * 增加订单标签
     */
    public void addOrderTab(){  
        Intent intent = new Intent();  
        intent.setClass(main.this, OrderList.class);     
        View v=populateTabItem(R.drawable.shopping_cart, "订单");
	    m_tabHost.addTab(m_tabHost.newTabSpec("menu2").setIndicator(v).setContent(intent));
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, Menu.FIRST + 1, 5, "退出登录").setIcon(this.getResources().getDrawable(R.drawable.logout));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case Menu.FIRST+1:
			LogOut();
			break;

		default:
			break;
		}
		return false;
	} 
	
	public void LogOut(){
		AlertDialog.Builder builder = new Builder(main.this);
		builder.setMessage("确定要退出登录吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DeleteUserInfo();
				Intent intent=new Intent(main.this,LoginPage.class);
		        startActivity(intent);
		        main.this.finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	public void DeleteUserInfo(){
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);
		userInfo.edit().putString("token","").commit();
	    userInfo.edit().putInt("restaurantid", 0).commit();
	    Declare_w declare_w=(Declare_w)this.getApplicationContext();
	    declare_w.loginResponse=null;
	}
	
    
}