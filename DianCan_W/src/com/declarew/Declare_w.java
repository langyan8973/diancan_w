package com.declarew;

import java.util.ArrayList;

import com.modelw.LoginResponse;
import com.modelw.ServiceMess;

import android.app.Application;

public class Declare_w extends Application {

	public boolean isMessPage=false;
	public ArrayList<ServiceMess> messArrayList=new ArrayList<ServiceMess>();
	public LoginResponse loginResponse;
}
