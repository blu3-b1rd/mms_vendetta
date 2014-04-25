package com.mms.model;

import org.json.JSONObject;

import com.mms.util.LoginType;

public class MMSAccount implements MMSModel {

	private static final long serialVersionUID = -6389279266533685290L;
	
	private LoginType loginType;
	private boolean isMainAccount;
	private String email;
	private String userName; // User account alias

	private MMSAccount() {}

	public LoginType getLoginType() {
		return loginType;
	}

	private void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}

	public boolean isMainAccount() {
		return isMainAccount;
	}

	private void setMainAccount(boolean isMainAccount) {
		this.isMainAccount = isMainAccount;
	}

	public String getEmail() {
		return email;
	}

	private void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	private void setUserName(String userName) {
		this.userName = userName;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_LOGIN_TYPE = "loginType";
		private final String TAG_IS_MAIN_ACCOUNT = "isMainAccount";
		private final String TAG_EMAIL = "email";
		private final String TAG_USER_NAME = "userName";
		
		public Builder(String modelType) {
			super(modelType);
		}
		
		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonAccount = new JSONObject(input);
			MMSAccount account = new MMSAccount();
			
			account.setLoginType(LoginType.values()[jsonAccount.getInt(TAG_LOGIN_TYPE)]);
			account.setMainAccount(jsonAccount.getBoolean(TAG_IS_MAIN_ACCOUNT));
			account.setEmail(jsonAccount.getString(TAG_EMAIL));
			account.setUserName(jsonAccount.getString(TAG_USER_NAME));
			
			return account;
		}

	}

}
