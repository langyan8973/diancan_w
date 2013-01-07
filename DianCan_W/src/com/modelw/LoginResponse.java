package com.modelw;

public class LoginResponse implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String token;
	private int restaurantid;
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
	
	
}
