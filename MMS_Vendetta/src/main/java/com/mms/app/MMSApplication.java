package com.mms.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.Session;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mms.activity.SplashActivity;
import com.mms.model.MMSUser;
import com.mms.util.LoginType;
import com.mms.vendetta.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.Map;

public class MMSApplication extends Application {
	
	private MMSUser user = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		MMSPreferences.init(this);
		this.initImageLoader(this);
	}
	
	private void initImageLoader(Context context){
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.discCacheSize(100 * 1024 * 1024)
			.discCacheFileNameGenerator(new Md5FileNameGenerator())
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();
		ImageLoader.getInstance().init(config);
	}
	
	public LoginType loadLoginType(){
		int loginIndex = MMSPreferences.loadInt(
				MMSPreferences.LOGIN_TYPE, -1);
		if(loginIndex < 0){
			return null;
		} else {
			return LoginType.values()[loginIndex];
		}
	}

	private void saveNewCredentials(String cookie, LoginType loginType){
		MMSPreferences.saveString(MMSPreferences.COOKIE, cookie);
		MMSPreferences.saveInt(MMSPreferences.LOGIN_TYPE, loginType.ordinal());
	}
	
	private void removeCredentials(){
		MMSPreferences.saveString(MMSPreferences.COOKIE, null);
		MMSPreferences.saveInt(MMSPreferences.LOGIN_TYPE, -1);
		MMSPreferences.saveString(MMSPreferences.DISPLAY_IMG, null);
		MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME, null);
	}
	
	public void setUser(MMSUser user){
		this.user = user;
		this.saveNewCredentials(this.user.getCookie(),
				this.user.getAccount().getLoginType());
		
	}
	
	public MMSUser getUser(){
		return this.user;
	}
	
	public void logout(Activity activity){
		Session fbSession = Session.getActiveSession();
		if(fbSession != null && fbSession.isOpened()){
			fbSession.closeAndClearTokenInformation();
		}
		this.removeCredentials();
		activity.startActivity(new Intent(activity, SplashActivity.class));
		activity.finish();
	}

    public enum TrackerName {
        APP_TRACKER(R.xml.app_tracker);

        private TrackerName(int trackerRes){ this.trackerRes = trackerRes; }

        private int trackerRes;
    }

    private Map<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId){
        if(!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(trackerId.trackerRes);
            mTrackers.put(trackerId, t);
        }

        return mTrackers.get(trackerId);
    }
	
}
