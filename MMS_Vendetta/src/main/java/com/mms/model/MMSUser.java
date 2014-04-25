package com.mms.model;

import org.json.JSONObject;

public class MMSUser implements MMSModel {

	private static final long serialVersionUID = -546037106981331707L;
	
	private int id;
	private String birthday;
	private String name;
	private String gender;
	private String cookie;
	private MMSAccount account;
	
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public String getBirthday() {
		return birthday;
	}

	private void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	private void setGender(String gender) {
		this.gender = gender;
	}

	public String getCookie() {
		return cookie;
	}

	private void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public MMSAccount getAccount() {
		return account;
	}

	private void setAccount(MMSAccount account) {
		this.account = account;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "userId";
		private final String TAG_BIRTHDAY = "birthday";
		private final String TAG_NAME = "name";
		private final String TAG_GENDER = "gender";
		private final String TAG_COOKIE = "cookie";
		private final String TAG_ACCOUNT = "account";
		
		public Builder(String modelType) {
			super(modelType);
		}
		
		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonUser = new JSONObject(input);
			MMSUser user = new MMSUser();
			
			user.setId(jsonUser.getInt(TAG_ID));
			user.setBirthday(jsonUser.getString(TAG_BIRTHDAY));
			user.setName(jsonUser.getString(TAG_NAME));
			user.setGender(jsonUser.getString(TAG_GENDER));
			user.setCookie(jsonUser.getString(TAG_COOKIE));
			user.setAccount((MMSAccount)(MMSModelFactory.getFactory()
					.getModel(TAG_ACCOUNT, jsonUser.getString(TAG_ACCOUNT))));
			
			return user;
		}

	}

}
