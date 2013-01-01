package com.modelw;

public class LoginResponse implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String token;
	private restaurant restaurant;
	public LoginResponse(){
		
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public restaurant getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(restaurant restaurant) {
		this.restaurant = restaurant;
	}
	
}
