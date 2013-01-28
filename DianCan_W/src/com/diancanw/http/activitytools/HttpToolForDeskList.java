package com.diancanw.http.activitytools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.os.Message;

import com.diancanw.declare.DiancanwApp;
import com.diancanw.http.HttpDownloader;
import com.diancanw.http.HttpHandler;
import com.diancanw.http.HttpRequestCallback;
import com.diancanw.model.Category;
import com.diancanw.model.Desk;
import com.diancanw.model.DeskType;
import com.diancanw.model.Order;
import com.diancanw.utils.JsonUtils;
import com.diancanw.utils.MenuUtils;

public class HttpToolForDeskList implements HttpRequestCallback{
	DiancanwApp mApp;
	HttpHandler httpHandler;
	DeskListHttpCallback deskListHttpCallback;
	public HttpToolForDeskList(DiancanwApp diancanwApp,DeskListHttpCallback httpCallback){
		mApp=diancanwApp;
		httpHandler=new HttpHandler(this);
		deskListHttpCallback=httpCallback;
	}
	@Override
	public void RequestComplete(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {  
        case HttpHandler.REQUEST_DESKTYPES: 
			List<DeskType> deskTypes=(List<DeskType>)msg.obj;
			SetDeskTypes(deskTypes);
            break;  
        case HttpHandler.REQUEST_DESKS:
        	List<Desk> desks=(List<Desk>)msg.obj;
        	SetDeskList(desks);
        	break;
        case HttpHandler.REQUEST_DESK:
        	String jsString=msg.obj.toString();
        	ResponseTable(jsString);
        	break;
        case HttpHandler.REQUEST_ORDER:
        	String strJs=msg.obj.toString();
        	ResponseOrder(strJs);
        	break;
        }
	}

	@Override
	public void RequestError(String errString) {
		// TODO Auto-generated method stub
		deskListHttpCallback.RequestError(errString);
	}
	
	public void RequestTypes(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<DeskType> types=MenuUtils.getDeskTypes(mApp.loginResponse.getRestaurantid());
				if(types==null||types.size()==0)
				{
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"获取餐桌类别失败！").sendToTarget();
				}
				else {
					httpHandler.obtainMessage(HttpHandler.REQUEST_DESKTYPES,types).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void SetDeskTypes(List<DeskType> deskTypes){
		deskListHttpCallback.SetDeskTypes(deskTypes);
	}
	
	public void RequestRecipeTypes(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					List<Category> categories=MenuUtils.getAllCategory(mApp.loginResponse.getRestaurantid());
					if(categories==null||categories.size()==0)
					{
						int what=HttpHandler.REQUEST_ERROR;
						httpHandler.obtainMessage(what,"获取菜品类别失败！").sendToTarget();
					}
					else {
						Iterator<Category> iterator;
						HashMap<String, String> cathash=new HashMap<String, String>();
						for(iterator=categories.iterator();iterator.hasNext();){
							Category category=iterator.next();
							cathash.put(category.getId().toString(), category.getName());
						}
						mApp.hashTypes=cathash;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					int what=HttpHandler.REQUEST_ERROR;
					httpHandler.obtainMessage(what,"获取菜品类别失败！").sendToTarget();
				}
				
			}
		}).start();
	}
	
	public void RequestDeskListByTid(final int typeid){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<Desk> deskList=MenuUtils.getDesksByTid(typeid,mApp.loginResponse.getRestaurantid());
				if(deskList==null||deskList.size()==0)
				{
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,"获取餐桌列表失败").sendToTarget();
				}
				else {
					
					httpHandler.obtainMessage(HttpHandler.REQUEST_DESKS,deskList).sendToTarget();
				}
			}
		}).start();
	}
	
	private void SetDeskList(List<Desk> desks){
		deskListHttpCallback.SetDeskList(desks);
	}
	
	public void RequestTable(final int id,final int count){
		//开台
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String resultString = HttpDownloader.submitOrder(MenuUtils.initUrl, id, count,
							mApp.loginResponse.getRestaurantid(),mApp.loginResponse.getToken()
							,mApp.udidString);
					httpHandler.obtainMessage(HttpHandler.REQUEST_DESK,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
				
			}
		}).start();
	}
	
	private void ResponseTable(String jsString){
		try {
			Order order=JsonUtils.ParseJsonToOrder(jsString);
			deskListHttpCallback.SetNewOrder(order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RequestError(e.getMessage());
		}
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
					httpHandler.obtainMessage(HttpHandler.REQUEST_ORDER,resultString).sendToTarget();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					httpHandler.obtainMessage(HttpHandler.REQUEST_ERROR,e.getMessage()).sendToTarget();
				}
			}
		}).start();
	}
	
	private void ResponseOrder(String strJs){
		try {
			Order order=JsonUtils.ParseJsonToOrder(strJs);
			deskListHttpCallback.SetOrder(order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RequestError(e.getMessage());
		}
	}
}
