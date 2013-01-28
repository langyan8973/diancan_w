package com.diancanw.http.activitytools;

import java.util.List;

import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.diancanw.OrderActivity;
import com.diancanw.OrderList;
import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.model.Order;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

public class HttpToolForOrderList implements HttpRequestCallback {

	DiancanwApp mApp;
	OrderListHttpCallback orderListHttpCallback;
	HttpHandler httpHandler;
	public HttpToolForOrderList(DiancanwApp diancanwApp,OrderListHttpCallback httpCallback){
		mApp=diancanwApp;
		orderListHttpCallback=httpCallback;
		httpHandler=new HttpHandler(this);
	}
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_ORDERS:
        	List<Order> orders=(List<Order>)msg.obj;
        	SetOrders(orders);
            break;  
        case HttpHandler.REQUEST_ORDERS_ORDER:
        	String strJs=msg.obj.toString();
        	ParseOrderByJs(strJs);
        	break;
        } 
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		orderListHttpCallback.RequestError(errString);
	}

	public void RequestOrders(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Order> orders=MenuUtils.getOrders(mApp.loginResponse.getRestaurantid(), mApp.loginResponse.getToken(),
						mApp.udidString);
				if(orders==null)
				{
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"获取订单列表失败！").sendToTarget();
				}
				else if(orders.size()==0) {
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"没有订单").sendToTarget();
				}
				else {
					httpHandler.obtainMessage(HttpHandler.REQUEST_ORDERS,orders).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void SetOrders(List<Order> orders){
		orderListHttpCallback.SetOrders(orders);
	}
	
	public void RequestOrderByOid(final String oidString){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+mApp.loginResponse.getRestaurantid()+"/orders/"+oidString,
							mApp.loginResponse.getToken(),mApp.udidString);
					if(resultString==null)
					{
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"请求订单失败！").sendToTarget();
						return;
					}
					httpHandler.obtainMessage(HttpHandler.REQUEST_ORDERS_ORDER,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	private void ParseOrderByJs(String jsString){
		try {
			System.out.println("resultString:"+jsString);
			Order order=JsonUtils.ParseJsonToOrder(jsString);
			orderListHttpCallback.SetSelectOrder(order);
		} catch (Throwable e) {
			e.printStackTrace();
			//自定义的错误，在界面上显示
			RequestError(e.getMessage());
            return;
		}
	}
}
