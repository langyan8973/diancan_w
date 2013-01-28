package com.diancanw.http.activitytools;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.Message;

import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.model.Order;
import com.diancanw.model.OrderItem;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

public class HttpToolForOrder implements HttpRequestCallback {
	DiancanwApp mApp;
	OrderHttpCallback orderHttpCallback;
	HttpHandler httpHandler;
	public HttpToolForOrder(DiancanwApp diancanwApp,OrderHttpCallback httpCallback){
		mApp=diancanwApp;
		orderHttpCallback=httpCallback;
		httpHandler=new HttpHandler(this);
	}
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_ORDER_REFRESH:
        	Order order=(Order)msg.obj;
        	RefreshOrderPage(order);
            break;  
        case HttpHandler.REQUEST_ORDER_CHECK:
        	CheckComplete();
        	break;
        case HttpHandler.REQUEST_ORDER_CANCEL:
        	CancelComplete();
        	break;
        case HttpHandler.REQUEST_ORDER_ITEMCHANGE:
        	String strJs=msg.obj.toString();
        	UpdateItem(strJs);
        	break;
        }  
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		orderHttpCallback.RequestError(errString);
	}

	public void RefreshOrder(final int oid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.getString(MenuUtils.initUrl+ "restaurants/"+mApp.loginResponse.getRestaurantid()+"/orders/"+oid,
							mApp.loginResponse.getToken(),mApp.udidString);
					if(resultString==null)
					{
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"订单刷新失败！").sendToTarget();
						return;
					}
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER_REFRESH,order).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	private void RefreshOrderPage(Order order){
		orderHttpCallback.UpdateOrderPage(order);
	}
	
	public void CheckOrder(final int oid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString;
				try {
					resultString = HttpDownloader.PutOrder(
							MenuUtils.initUrl+ "restaurants/"+mApp.loginResponse.getRestaurantid()+"/orders/"+oid+"/check",
							mApp.loginResponse.getToken(),mApp.udidString);
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					if(order!=null&&order.getStatus()==4){
						httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER_CHECK,"").sendToTarget();
					}
					else {
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"结账失败！").sendToTarget();
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void CheckComplete(){
		orderHttpCallback.OrderChecked();
	}
	
	public void CancelOrder(final int oid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString;
				try {
					resultString = HttpDownloader.PutOrder(
							MenuUtils.initUrl+ "restaurants/"+mApp.loginResponse.getRestaurantid()+"/orders/"+oid+"/cancel",
							mApp.loginResponse.getToken(),mApp.udidString);
					Order order=JsonUtils.ParseJsonToOrder(resultString);
					if(order!=null&&order.getStatus()==5){
						httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER_CANCEL,"").sendToTarget();
					}
					else {
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"撤单失败！").sendToTarget();
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void CancelComplete(){
		orderHttpCallback.OrderCancelled();
	}
	
	public void ChangeItemStatus(final int iid,final int oid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String resultString;
				try {
					resultString = HttpDownloader.PutOrder(
							MenuUtils.initUrl+ "restaurants/"+mApp.loginResponse.getRestaurantid()+"/orders/"+oid+"/"+iid,
							mApp.loginResponse.getToken(),mApp.udidString);
					OrderItem orderItem=JsonUtils.ParseJsonToOrderItem(resultString);
					if(orderItem!=null&&orderItem.getStatus()==1){
						httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER_ITEMCHANGE,""+iid).sendToTarget();
					}
					else {
						httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"修改状态失败！").sendToTarget();
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void UpdateItem(String idString){
		orderHttpCallback.UpdateOrderItemById(idString);
	}
}
