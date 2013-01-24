package com.diancanw.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtils {
	public static File cacheDir;
	public FileUtils() {
		
		
	}
	public static void SaveUserInfo(String name,String pass,integer restaurantid) throws IOException
	{
//		context.deleteFile("dingdan.txt");
//		FileOutputStream outputStream=context.openFileOutput("dingdan.txt", Context.MODE_APPEND);
//        outputStream.write(str.getBytes());  
//        outputStream.close();
	}
	public static String ReadUserInfo() throws IOException
	{
		return null;
//		FileInputStream inputStream=context.openFileInput("dingdan.txt");  
//        ByteArrayOutputStream outStream=new ByteArrayOutputStream();  
//        byte[] buffer=new byte[1024];  
//        int len=0;  
//        while ((len=inputStream.read(buffer))!=-1){  
//            outStream.write(buffer, 0, len);  
//        }  
//        outStream.close();  
//        byte[] data=outStream.toByteArray();  
//        String jsonstr=new String(data);  
//        return jsonstr; 
	}
}