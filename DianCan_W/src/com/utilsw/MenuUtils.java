package com.utilsw;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.httpw.HttpDownloader;
import com.modelw.AllDomain;
import com.modelw.Category;
import com.modelw.Desk;
import com.modelw.DeskType;
import com.modelw.MessContent;
import com.modelw.Recipe;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MenuUtils {
	public static final int CMD_STOP_SERVICE = 0;
	public static final String UPDATE_COMPLETE = "updatecomplete";
	public static final String DOWNLOAD_COMPLETE = "downloadcomplete";
	public static final String UPDATE_SERVICE = "com.chihuo.UpdateService";
	public static String initUrl;
	public static String updateUrl;
	public static String imageUrl;
	public static boolean bUpdating = false;
	public static String XIAOMI="Xiaomi";
	public static String MIONE="MI-ONE Plus";
	public static boolean ISXIAOMI;
	public static String Service_1="加餐具";
	public static String Service_2="加水";
	public static String Service_3="服务员";
	
	public static ArrayList<String> Login(int code,String name,String password) throws Throwable{
		
		ArrayList<String> arr=HttpDownloader.UserLogin(code, name, password, initUrl+"wlogin");
		return arr;
	}

	/***
	 * 获取所有菜类别
	 * @return
	 */
	public static List<Category> getAllCategory() {
		String urlString = initUrl + "categories";
		String jsonStr = HttpDownloader.getString(urlString,null);
		System.out.println(jsonStr);

		Type objType = new TypeToken<List<Category>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Category> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	/***
	 * 获取某个种类的所有菜
	 * @param id
	 * @return
	 */
	public static List<Recipe> getRecipesByCategory(Integer id) {
		String urlString = initUrl + "categories/" + id;

		String jsonStr = HttpDownloader.getString(urlString,null);

		Type objType = new TypeToken<List<Recipe>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Recipe> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	/***
	 * 获取所有桌子
	 * @return
	 */
	public static List<Desk> getAllDesks() {
		String urlString = initUrl + "desks";

		String jsonStr = HttpDownloader.getString(urlString,null);

		Type objType = new TypeToken<List<Desk>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Desk> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	/***
	 * 获取所有餐桌分类
	 * @return
	 */
	public static List<DeskType> getDeskTypes(int id)
	{
		String urlString = initUrl + "restaurants/"+id+"/desktypes";

		String jsonStr = HttpDownloader.getString(urlString,null);

		Type objType = new TypeToken<List<DeskType>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<DeskType> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	/***
	 * 获取某一种类的所有餐桌
	 * @param id
	 * @return
	 */
	public static List<Desk> getDesksByTid(int id,int rid)
	{
		String urlString = initUrl + "restaurants/"+rid+"/desks?tid="+id;

		String jsonStr = HttpDownloader.getString(urlString,null);

		Type objType = new TypeToken<List<Desk>>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		List<Desk> infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}

	public static AllDomain DownloadMenusData(String strdate) {
		String urlString = initUrl + "all";
		if (strdate != "") {
			urlString += "/" + strdate;
		}
		String jsonStr = HttpDownloader.getString(urlString,null);
		// System.out.println(jsonStr);

		Type objType = new TypeToken<AllDomain>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		AllDomain infos = sGson.fromJson(jsonStr, objType);
		return infos;
	}
	
	public static MessContent getMessContentByjs(String jsString)
	{
		Type objType = new TypeToken<MessContent>() {
		}.getType();
		Gson sGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS")
				.create();
		MessContent infos = sGson.fromJson(jsString, objType);
		return infos;
	}

	

	/**
	   * 以最省内存的方式读取本地资源的图片
	   * @param context
	   * @param resId
	   * @return
	   */  
	public static Bitmap readBitMap(Context context, int resId,int size){  
	   BitmapFactory.Options opt = new BitmapFactory.Options();  
	   opt.inPreferredConfig = Bitmap.Config.RGB_565;   
	   opt.inPurgeable = true;  
	   opt.inInputShareable = true;  
	   opt.inSampleSize=size;
	   //获取资源图片  
	   InputStream is = context.getResources().openRawResource(resId);  
	   return BitmapFactory.decodeStream(is,null,opt);  
	 }
	
	/***
	 * 回收图片所占内存
	 * @param bmp
	 */
	public static void Recycled(Bitmap bmp)
	{
		if(!bmp.isRecycled() ){
			bmp.recycle();   //回收图片所占的内存
	         System.gc();    //提醒系统及时回收
		}
	}
}
