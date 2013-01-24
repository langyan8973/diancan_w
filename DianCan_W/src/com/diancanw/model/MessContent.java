package com.diancanw.model;

public class MessContent implements java.io.Serializable {
	/**
	 * 
	 */
	public int id;
	public int oid;
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
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	
}
