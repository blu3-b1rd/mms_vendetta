package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

import com.mms.util.LoginType;

public class CreateUserRequest extends BaseRequest {

	private static final String PARAM_NAME = "name";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_GENDER = "gender";
	private static final String PARAM_LOGIN_TYPE = "loginType";
	private static final String PARAM_BIRTHDAY = "birthday";
	private static final String PARAM_PASSWORD = "password";
	
	public CreateUserRequest(String name, String email,
			String username, String gender, LoginType loginType,
			String birthday) {
		super("user/create", RequestType.HTTP_POST);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_NAME, name));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_EMAIL, email));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_USERNAME, name));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_GENDER, gender));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_LOGIN_TYPE, loginType.alias));
		if(birthday != null){
			this.paramsDictionary.add(new BasicNameValuePair(PARAM_BIRTHDAY,
					birthday.replace("-", "/")));
		}
	}
	
	public CreateUserRequest(String name, String email,
			String username, String gender, LoginType loginType,
			String birthday, String password) {
		this(name, email, username, gender, loginType, birthday);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_PASSWORD, password));
	}

}
