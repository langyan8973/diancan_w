package com.diancanw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.customw.CustomViewBinder;
import com.declarew.Declare_w;
import com.httpw.HttpDownloader;
import com.modelw.Desk;
import com.modelw.Order;
import com.utilsw.DisplayUtil;
import com.utilsw.JsonUtils;
import com.utilsw.MenuUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OrderList extends Activity {

	List<Order> orders;
	GridView mGridView;
	RelativeLayout contentLayout;
	ProgressBar mProgress;
	
	ArrayList<HashMap<String, Object>> imghashList;
	SimpleAdapter gridSimpleAdapter;
	Declare_w m_Declare;
	
	private Handler httpHandler = new Handler() {  
        public void handleMessage (Message msg) {//此方法在ui线程运行   
            switch(msg.what) {  
            case 0: 
            	String errString=msg.obj.toString();
            	ShowError(errString);
                break;   
            case 1:
            	DisplayGrid();
                break;  
            case 2:
            	String strJs=msg.obj.toString();
            	ResponseOrder(strJs);
            	break;
            case 3:
            	String jsString=msg.obj.toString();
            	break;
            case 4:
            	String strJs1=msg.obj.toString();
            	break;
            }  
        }  
    }; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderlist);
        
        contentLayout=(RelativeLayout)findViewById(R.id.OrderContent);
        mGridView=new GridView(this);
		mGridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mGridView.setNumColumns(GridView.AUTO_FIT);
		mGridView.setVerticalSpacing(DisplayUtil.dip2px(6.7f));
		mGridView.setHorizontalSpacing(DisplayUtil.dip2px(6.7f));
		mGridView.setColumnWidth(DisplayUtil.dip2px(80));
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);	
		mGridView.setOnItemClickListener(new griditemclick());
		contentLayout.addView(mGridView);
		
		mProgress=(ProgressBar)findViewById(R.id.orderpro);
		mProgress.setVisibility(View.INVISIBLE);
		
		m_Declare=(Declare_w)this.getApplicationContext();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GetOrders();
	}

	/**
	 * 获取所有订单
	 */
	public void GetOrders(){
		mProgress.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				orders=MenuUtils.getOrders(m_Declare.loginResponse.getRestaurantid(), m_Declare.loginResponse.getToken(),
						m_Declare.udidString);
				if(orders==null)
				{
					httpHandler.obtainMessage(0,"获取订单列表失败！").sendToTarget();
				}
				else if(orders.size()==0) {
					httpHandler.obtainMessage(0,"没有订单").sendToTarget();
				}
				else {
					httpHandler.obtainMessage(1).sendToTarget();
				}
				
			}
		}).start();
	}
	
	/**
	 * 显示订单表
	 */
	public void DisplayGrid(){
		mProgress.setVisibility(View.INVISIBLE);
		if(imghashList==null){
			imghashList=new ArrayList<HashMap<String,Object>>();
		}
		imghashList.clear();
		
		Iterator<Order> iterator;
		
		for (iterator = orders.iterator(); iterator.hasNext();) {
			Order order = iterator.next();
			if(order.getStatus()==4||order.getStatus()==5)
			{
				continue;
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", order.getDesk().getName());
  			map.put("id", order.getId());
			Bitmap bmp;
			if(order.getDesk().getCapacity()<=4)
			{
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
			else if(order.getDesk().getCapacity()>4&&order.getDesk().getCapacity()<=10){
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
			else {
				bmp = MenuUtils.readBitMap(this, R.drawable.mytable, 1);
			}
  			map.put("simg", bmp);
  			
  			imghashList.add(map);
		}
		
		if(imghashList.size()>=0){
			gridSimpleAdapter=CreategridAdapter(imghashList);
			mGridView.setAdapter(gridSimpleAdapter);
		}
		
	}
	
	/***
	 * 创建表格数据源适配器
	 * @param list
	 * @return
	 */
	public SimpleAdapter CreategridAdapter(ArrayList<HashMap<String, Object>> list)
	{
		SimpleAdapter saImageItems = new SimpleAdapter(this,  
				list,
                R.layout.ordergriditem, 
                new String[] { "simg", "title"},  
                new int[] { R.id.deskImage, R.id.deskTitle}){
        };  

        saImageItems.setViewBinder(new CustomViewBinder());
		return saImageItems;
	}
	
	/**
	 * 通过id获取订单
	 * @param oidString
	 */
	public void RequestOrderByOid(final String oidString)
	{
		mProgress.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+m_Declare.loginResponse.getRestaurantid()+"/orders/"+oidString,
							m_Declare.loginResponse.getToken(),m_Declare.udidString);
					if(resultString==null)
					{
						httpHandler.obtainMessage(0,"编码错误！").sendToTarget();
						return;
					}
					httpHandler.obtainMessage(2,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(0,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	/**
	 * 解析订单并跳转到订单页
	 * @param jsString
	 */
	public void ResponseOrder(String jsString)
	{
		mProgress.setVisibility(View.INVISIBLE);
		try {
			System.out.println("resultString:"+jsString);
			Order order=JsonUtils.ParseJsonToOrder(jsString);
			
		    Intent intent=new Intent(this, OrderActivity.class);
		    intent.putExtra("order", order);
		    startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			Toast toast = Toast.makeText(OrderList.this, e.getMessage(), Toast.LENGTH_SHORT); 
            toast.show();
            return;
		}
	}
	
	/**
	 * 显示错误信息
	 * @param strMess
	 */
	public void ShowError(String strMess) {
		mProgress.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(OrderList.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}
	
	/**
	 * 点击表格项
	 * @author liuyan
	 *
	 */
	class griditemclick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			String oidString=item.get("id").toString();
	    	RequestOrderByOid(oidString);
		}
		
	}

}
