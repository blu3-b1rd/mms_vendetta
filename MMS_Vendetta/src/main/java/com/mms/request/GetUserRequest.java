package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

import com.mms.util.LoginType;

public class GetUserRequest extends BaseRequest {
	
	private static final String PARAM_COOKIE = "cookie";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_LOGIN_TYPE = "loginType";
	private static final String PARAM_PASSWORD = "password";
	
	public GetUserRequest(LoginType loginType, String email) {
		super("user", RequestType.HTTP_POST);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_LOGIN_TYPE, loginType.alias));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_EMAIL, email));
	}
	
	public GetUserRequest(LoginType loginType, String email, String password) {
		this(loginType, email);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_PASSWORD, password));
	}
	
	public GetUserRequest(String cookie, LoginType loginType){
		super("user/bycookie", RequestType.HTTP_POST);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_LOGIN_TYPE, loginType.alias));
	}

}
