package com.utilsw;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.modelw.Order;


public class JsonUtils {
//	public static String ConvertDeskObjToJson(DeskObj deskObj)
//	{
//		Gson sGson=new Gson();
//		String deskString=sGson.toJson(deskObj);
//		return deskString;
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
//		Gson sGson=new Gson();
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
}
