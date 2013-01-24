package com.diancanw.model;

import android.R.integer;

public class LoginResponse implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String token;
	private int restaurantid;
	private int waiterid;
	public LoginResponse(){
		
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getRestaurantid() {
		return restaurantid;
	}
	public void setRestaurantid(int restaurantid) {
		this.restaurantid = restaurantid;
	}
	public int getWaiterid() {
		return waiterid;
	}
	public void setWaiterid(int waiterid) {
		this.waiterid = waiterid;
	}
	
	
}
