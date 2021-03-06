package com.diancanw.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.diancanw.model.LoginResponse;
import com.diancanw.model.Notify;
import com.diancanw.model.Order;
import com.diancanw.model.OrderItem;
import com.diancanw.model.restaurant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


public class JsonUtils {
	/***
	 * 将user对象转成js串
	 * @param user
	 * @return
	 */
//	public static String ConvertUserObjToJson(User user)
//	{
//		Gson sGson=new Gson();
//		String userString=sGson.toJson(user);
//		return userString;
//	}
//	public static DeskObj ParseJsonToDeskObj(String jsonStr)
//	{
//		Type objType=new TypeToken<DeskObj>() {
//		}.getType();
//		Gson sGson=new Gson();
//		DeskObj deskObj=sGson.fromJson(jsonStr, objType);
//		return deskObj;
//	}
//	public static String ConvertHistoryToJson(History history)
//	{
//		Gson sGson=new Gson();
//		String hiString=sGson.toJson(history);
//		return hiString;
//	}
//	public static History ParseJsonToHistory(String jsonStr)
//	{
//		Type objType=new TypeToken<History>() {
//		}.getType();
//		Gson sGson=new Gson();
//		History history=null;
//		try {
//			history = sGson.fromJson(jsonStr, objType);
//		} catch (JsonSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return history;
//	}
	/***
	 * 将json串转换成order对象
	 * @param jsonStr
	 * @return
	 */
	public static Order ParseJsonToOrder(String jsonStr)
	{
		Type objType=new TypeToken<Order>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		Order order=null;
		try {
			order = sGson.fromJson(jsonStr, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return order;
	}
	
	/***
	 * 将json串转换成orderitem对象
	 * @param jsonStr
	 * @return
	 */
	public static OrderItem ParseJsonToOrderItem(String jsonStr)
	{
		Type objType=new TypeToken<OrderItem>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		OrderItem orderItem=null;
		try {
			orderItem = sGson.fromJson(jsonStr, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return orderItem;
	}
	
	/***
	 * 将json串转换成restaurant对象
	 */
	public static restaurant parseJsonToRestaurant(String jsonString){
		Type objType=new TypeToken<restaurant>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		restaurant restaurant=null;
		try {
			restaurant = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return restaurant;
	}
	
	/**
	 * 将LoginResponse对象转换成json串
	 * @param LoginResponse
	 * @return
	 */
	public static String ConvertLoginResponseToJson(LoginResponse loginResponse)
	{
		Gson sGson=new Gson();
		String loginString=sGson.toJson(loginResponse);
		return loginString;
	}
	
	/***
	 * 将json串转换成LoginResponse对象
	 */
	public static LoginResponse parseJsonToLoginResponse(String jsonString){
		Type objType=new TypeToken<LoginResponse>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		LoginResponse loginResponse=null;
		try {
			loginResponse = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginResponse;
	}
	
	public static Notify parseJsonToNotify(String jsonString){
		Type objType=new TypeToken<Notify>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		Notify notify=null;
		try {
			notify = sGson.fromJson(jsonString, objType);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notify;
	}
}
