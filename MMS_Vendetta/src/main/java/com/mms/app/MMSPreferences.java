package com.mms.app;

import android.content.Context;
import android.content.SharedPreferences;

public class MMSPreferences {

	private static final String PREFS_NAME = "mms_" + MMSConfig.ARTIST_ALIAS + "_prefs";
	
	public static final String COOKIE = "cookie";
	public static final String LOGIN_TYPE = "login_type";
	public static final String PASSWORD = "password";
	public static final String DISPLAY_IMG = "display_img";
	public static final String DISPLAY_NAME = "display_name";

	private static SharedPreferences preferences = null;
	private static SharedPreferences.Editor editor = null;

	public enum RegisterStep {
		UNREGISTERED, PIN_VERIFICATION, SOCIAL_NETWORKS, REGISTERED;
	}

	public static void init(Context context) {
		preferences = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public static String loadString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

	public static int loadInt(String key, int defValue) {
		return preferences.getInt(key, defValue);
	}

	public static boolean loadBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public static void saveString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public static void saveInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public static void saveBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

}
