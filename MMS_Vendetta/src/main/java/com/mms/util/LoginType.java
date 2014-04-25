package com.mms.util;

public enum LoginType {
	FACEBOOK("facebook"), EMAIL("mail"), GOOGLE_PLUS("google");
	
	public String alias;
	
	private LoginType(String alias){
		this.alias = alias;
	}
	
}
