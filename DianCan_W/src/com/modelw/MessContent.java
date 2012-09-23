package com.modelw;

public class MessContent implements java.io.Serializable {
	/**
	 * 
	 */
	public int id;
	public String name;
	public MessContent()
	{
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
