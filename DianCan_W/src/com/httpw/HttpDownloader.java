package com.httpw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.modelw.Order;
import com.modelw.OrderItem;
import com.utilsw.FileUtils;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;


public class HttpDownloader {
	/***
	 * 发送请求获取返回的字符串
	 * @param urlStr
	 * @return
	 */
	public static String getString(String urlStr,String token,String udid) {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient client= new DefaultHttpClient();
		HttpGet get = new HttpGet(urlStr);
		
		if(token!=null)
		{
			get.addHeader("Authorization", token);
			get.addHeader("X-device",udid);
		}
		else {
			get.addHeader("accept", "application/json;charset=UTF-8");
			get.addHeader("Accept-Charset", "utf-8");
		}
		try {
			HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(inputStream,
								Charset.forName("utf-8")));
				String line = null;
				while ((line = buffer.readLine()) != null) {
					sb.append(line);
				}
				inputStream.close();
				return sb.toString();
			} else {
				// TODO 返回错误信息
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			// TODO 返回协议错误信息
		} catch (IOException e) {
			e.printStackTrace();
			// TODO 返回网络错误
		}
		return null;
	}
	
	/***
	 * 请求图片
	 * @param urlStr
	 * @return
	 */
	public static Bitmap getStream(String urlStr ) {		
		try {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(true);
//			BitmapFactory.Options options=new BitmapFactory.Options();
//			options.inSampleSize=2;
//			Bitmap bmp=BitmapFactory.decodeStream(connection.getInputStream(),null,options);
			InputStream iStream=connection.getInputStream();
			Bitmap bmp=BitmapFactory.decodeStream(iStream);
			iStream.close();
			return bmp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	//启用缓存
	public static void enableHttpResponseCache() {
	  try {
	    long httpCacheSize = 50 * 1024 * 1024; // 10 MiB
	    File httpCacheDir = new File(FileUtils.cacheDir.getAbsolutePath(), "http");
	    Class.forName("android.net.http.HttpResponseCache")
	         .getMethod("install", File.class, long.class)
	         .invoke(null, httpCacheDir, httpCacheSize);
	  } catch (Exception httpResponseCacheNotAvailable) {
		  ResponseCache.setDefault(new MyResponseCache2());
	  }
	}

	/***
	 * 提交订单
	 * @param rootUrl
	 * @param order
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String OrderForm(String rootUrl,Order order) throws ClientProtocolException, IOException,
		JSONException {
		
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		System.out.println("提交订单:");
		HttpPost post = new HttpPost(rootUrl + "orders");
		
		JSONArray list = new JSONArray();
		Iterator<OrderItem> iterator;
		for(iterator=order.getOrderItems().iterator();iterator.hasNext();)
		{
			OrderItem orderItem=iterator.next();
			JSONObject obj = new JSONObject();
			obj.put("rid", orderItem.getRecipe().getId());
			obj.put("count",orderItem.getCount());
			list.put(obj);
		}
		
		JSONObject object = new JSONObject();
		object.put("tid",order.getDesk().getId());
		object.put("number", order.getNumber());
		object.put("recipes", list);
		System.out.println(object.toString());
		StringEntity entity = new StringEntity(object.toString(), "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		
		HttpClientParams.setRedirecting(post.getParams(), false);
		
		HttpResponse response = client.execute(post);
		junit.framework.Assert.assertEquals(201, response.getStatusLine().getStatusCode());
		
		String location = response.getLastHeader("Location").getValue();
		System.out.println("创建成功：" + location);
		return location;
	}
	
	/***
	 * 获取订单
	 * @param reqString
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public static String GetOrderForm(String reqString) throws ClientProtocolException, IOException,
	JSONException {
		System.out.println("获取单个订单:");
		DefaultHttpClient client;
		client = new DefaultHttpClient();
		
		HttpGet get = new HttpGet(reqString);
		get.addHeader("accept", "application/json;charset=UTF-8");
		get.addHeader("Accept-Charset", "utf-8");
		HttpResponse response = client.execute(get);
				
		HttpEntity entity = response.getEntity();
		System.out.println(entity.getContentType().getValue());
		String jsonString=parseContent(entity.getContent());
		System.out.println(jsonString);
		return jsonString;
	}
	
		//开台
		public static String submitOrder(String rootUrl,int tid,int number,int rid,String token,String udid) throws Throwable {
			
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("did", new StringBody(tid+""));
			entity.addPart("number", new StringBody(number+""));			
			String rUrl=rootUrl + "restaurants/"+rid+"/orders";
			HttpPost post = new HttpPost(rUrl);
			post.addHeader("Authorization", token);
			post.addHeader("X-device",udid);
			post.setEntity(entity);
			
			HttpClientParams.setRedirecting(post.getParams(), false);
			DefaultHttpClient client= new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String jsonString=parseContent(responseEntity.getContent());
			System.out.println(jsonString);
			
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 201) {
				return jsonString;
			}
			else {
				throw new Exception(jsonString);
			}
		}
		
		/**
		 * 结账、撤单或改变订单项状态请求
		 * @param urlString
		 * @param token
		 * @return
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 */
		public static String PutOrder(String urlString,String token,String udid) throws ClientProtocolException, IOException{
			DefaultHttpClient client;
			client = new DefaultHttpClient();
			
			HttpPut put = new HttpPut(urlString);
			put.addHeader("Authorization", token);
			put.addHeader("X-device",udid);
			HttpResponse response = client.execute(put);
					
			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType().getValue());
			String jsonString=parseContent(entity.getContent());
			System.out.println(jsonString);
			return jsonString;
		}
		
		
		/***
		 * 加减菜
		 * @param rootUrl
		 * @param oid
		 * @param object
		 * @return
		 * @throws Throwable
		 */
		public static String alterRecipeCount(String rootUrl, int oid,JSONObject object) throws Throwable
		{
			StringEntity entity = new StringEntity(object.toString(), "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");
			entity.setContentEncoding("UTF-8");
			
			HttpPost post = new HttpPost(rootUrl + "orders/" + oid);
			post.setEntity(entity);
			post.setHeader("Content-Type", "application/json;charset=UTF-8");
			
			HttpClientParams.setRedirecting(post.getParams(), true);
			DefaultHttpClient client= new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String jsonString=parseContent(responseEntity.getContent());
			
			if (response.getStatusLine().getStatusCode() == 200) {
				return jsonString;
			}
			else {
				throw new Exception(jsonString);
			}
			
		}
		
		public static String ChangeOrderItemStatus(String reqString) throws Throwable
		{
			
			HttpPost post = new HttpPost(reqString);
			post.setHeader("Content-Type", "application/json;charset=UTF-8");
			
			HttpClientParams.setRedirecting(post.getParams(), true);
			DefaultHttpClient client= new DefaultHttpClient();
			HttpResponse response = client.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String jsonString=parseContent(responseEntity.getContent());
			
			if (response.getStatusLine().getStatusCode() == 200) {
				return jsonString;
			}
			else {
				throw new Exception(jsonString);
			}
		}
		
		public static ArrayList<String> UserLogin(int id,String name,String password,String udid,String strurl) throws Throwable{ 
			
			
			  DefaultHttpClient client= new DefaultHttpClient();
			  HttpPost httppost = new HttpPost(strurl); 
			  httppost.addHeader("X-device", udid);
			  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
			   nameValuePairs.add(new BasicNameValuePair("restaurant", id+""));
			   nameValuePairs.add(new BasicNameValuePair("username", name)); 
			   nameValuePairs.add(new BasicNameValuePair("password", password)); 
	
			   httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	
			   HttpResponse response; 
			   response=client.execute(httppost); 
			   HttpEntity responseEntity = response.getEntity();
			   ArrayList<String> mList=new ArrayList<String>();
			   String auth = response.getFirstHeader("Authorization").getValue();
			   String jsonString=parseContent(responseEntity.getContent());
			   mList.add(auth);
			   mList.add(jsonString);
				if (response.getStatusLine().getStatusCode() == 200) {
					return mList;
				}
				else {
					throw new Exception(jsonString);
				} 
		} 
		
		public static String RegisterUdid(String id,String strurl) throws ClientProtocolException, IOException{
			  DefaultHttpClient client= new DefaultHttpClient();
			  HttpPost httppost = new HttpPost(strurl); 
			  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1); 
			   nameValuePairs.add(new BasicNameValuePair("udid", id+""));
			   httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
	
			   HttpResponse response; 
			   response=client.execute(httppost); 
			   HttpEntity responseEntity = response.getEntity();
			   String jsonString=parseContent(responseEntity.getContent());
				return jsonString;
		}
		
		private static String parseContent(InputStream stream) throws IOException {
			StringBuilder sb = new StringBuilder();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(stream));
			String line = reader.readLine();
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
			reader.close();
			return sb.toString();
		}
	
}
