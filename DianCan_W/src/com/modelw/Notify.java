package com.modelw;

import java.io.Serializable;

import android.R.integer;

public class Notify implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int oid;
	public Notify(){
		
	}
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	

}
