package com.diancanw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.diancanw.custom.CustomViewBinder;
import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.http.activitytools.HttpToolForOrderList;
import com.diancanw.http.activitytools.OrderListHttpCallback;
import com.diancanw.model.Desk;
import com.diancanw.model.Order;
import com.diancanw.utils.DisplayUtil;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

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

public class OrderList extends Activity implements OrderListHttpCallback,OnItemClickListener {

	List<Order> orders;
	GridView mGridView;
	ProgressBar mProgress;
	
	ArrayList<HashMap<String, Object>> imghashList;
	SimpleAdapter gridSimpleAdapter;
	DiancanwApp m_Declare;
	HttpToolForOrderList httpToolForOrderList;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.orderlist);
        mGridView=(GridView)findViewById(R.id.ordersGrid);
		mGridView.setOnItemClickListener(this);
		
		mProgress=(ProgressBar)findViewById(R.id.orderpro);
		mProgress.setVisibility(View.INVISIBLE);
		
		m_Declare=(DiancanwApp)this.getApplicationContext();
		httpToolForOrderList=new HttpToolForOrderList(m_Declare, this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GetOrders();
	}
	
	@Override
	public void SetOrders(List<Order> orderlist) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);
		orders=orderlist;
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
				bmp = MenuUtils.readBitMap(this, R.drawable.shopping_cart, 1);
			}
			else if(order.getDesk().getCapacity()>4&&order.getDesk().getCapacity()<=10){
				bmp = MenuUtils.readBitMap(this, R.drawable.shopping_cart, 1);
			}
			else {
				bmp = MenuUtils.readBitMap(this, R.drawable.shopping_cart, 1);
			}
  			map.put("simg", bmp);
  			
  			imghashList.add(map);
		}
		
		if(imghashList.size()>=0){
			gridSimpleAdapter=CreategridAdapter(imghashList);
			mGridView.setAdapter(gridSimpleAdapter);
		}
		
	}

	@Override
	public void SetSelectOrder(Order order) {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.INVISIBLE);
		try {
		    Intent intent=new Intent(this, OrderActivity.class);
		    intent.putExtra("order", order);
		    startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			ShowError(e.getMessage());
            return;
		}
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		String oidString=item.get("id").toString();
    	RequestOrderByOid(oidString);
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		ShowError(errString);
	}

	/**
	 * 获取所有订单
	 */
	public void GetOrders(){
		mProgress.setVisibility(View.VISIBLE);
		httpToolForOrderList.RequestOrders();
	}

	/**
	 * 通过id获取订单
	 * @param oidString
	 */
	public void RequestOrderByOid(final String oidString)
	{
		mProgress.setVisibility(View.VISIBLE);
		httpToolForOrderList.RequestOrderByOid(oidString);
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
	 * 显示错误信息
	 * @param strMess
	 */
	public void ShowError(String strMess) {
		mProgress.setVisibility(View.INVISIBLE);
		Toast toast = Toast.makeText(OrderList.this, strMess, Toast.LENGTH_SHORT); 
        toast.show();
	}

	
}
