package com.mms.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSResponse;
import com.mms.model.MMSUser;
import com.mms.request.BaseRequest;
import com.mms.request.CreateUserRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.util.LoginType;
import com.mms.vendetta.R;

public class SplashActivity extends ActionBarActivity implements AnimationListener, OnPageChangeListener,
	Handler.Callback, OnConnectionFailedListener, ConnectionCallbacks, Session.StatusCallback,
	OnMMSRequestFinishedListener {
	
	private MMSApplication mmsApplication;
	
	private int[] imagesIds = {R.drawable.splash_1, R.drawable.splash_2,
			R.drawable.splash_3, R.drawable.splash_4,
			R.drawable.splash_5, R.drawable.splash_6, R.drawable.splash_7 };
	private int[] animIds = {R.anim.appear_to_bottom_right,
			R.anim.appear_to_bottom_right, R.anim.appear_to_bottom_right};
	
	private ViewPager imagesPager;
	private boolean isInitiated = false;
	private PlusClient mPlusClient;
	private UiLifecycleHelper mFbUiHelper;
	
	public static final int MESSAGE_INITIATE = 0;
	public static final int MESSAGE_SHOW_REGISTER = 1;
	public static final int MESSAGE_LOGGED = 2;
	
	private final int REQUEST_CODE_RESOLVE_ERR = 100;
	//private final int REAUTH_FB_ACTIVITY_CODE = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		this.mmsApplication = (MMSApplication) this.getApplication();
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		if(cookie != null){
			this.goToHomeScreen();
		} else {
			this.init();
			this.mFbUiHelper.onCreate(savedInstanceState);
		}
	}
	
	private void init(){
		this.findViewById(R.id.google_login).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onGooglePlusClicked();
			}
		});
		this.imagesPager = (ViewPager) this.findViewById(R.id.images_slider);
		this.imagesPager.setAdapter(new SplashImagesAdapter(this.getSupportFragmentManager()));
		this.imagesPager.setOnPageChangeListener(this);
		this.mPlusClient = new PlusClient.Builder(this, this, this)
			.setActions("http://schemas.google.com/AddActivity").build();
		((LoginButton) this.findViewById(R.id.facebook_login)).setReadPermissions(
				this.getResources().getStringArray(R.array.fb_read_permissions));
		this.mFbUiHelper = new UiLifecycleHelper(this, this);

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.mFbUiHelper.onResume();
		this.overridePendingTransition(R.anim.push_back_in, R.anim.push_back_out);
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		if(cookie != null){
			this.goToHomeScreen();
		}
	}
	
	private void startPagerAnimation(){
		Random randomGenerator = new Random(Calendar.getInstance().getTimeInMillis());
		Animation anim = AnimationUtils.loadAnimation(this,
				this.animIds[randomGenerator.nextInt(animIds.length)]);
		anim.setAnimationListener(this);
		SplashImagesAdapter adapter = (SplashImagesAdapter) this.imagesPager.getAdapter();
		adapter.getFragment(this.imagesPager.getCurrentItem()).animateImage(anim);
	}
	
	@Override
	public void onPageSelected(int position) {
		this.startPagerAnimation();
	}
	@Override public void onPageScrolled(int arg0, float arg1, int arg2) {}
	@Override public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onAnimationEnd(Animation animation) {
		try {
			Random randomGenerator = new Random(Calendar.getInstance().getTimeInMillis());
			int nextItem = 0;
			do {
				nextItem = randomGenerator.nextInt(this.imagesIds.length-1);
			} while(nextItem == this.imagesPager.getCurrentItem());
			final int ni = nextItem;
			this.imagesPager.post(new Runnable() {
				@Override
				public void run() {
					imagesPager.setCurrentItem(ni);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override public void onAnimationRepeat(Animation animation) {}
	@Override public void onAnimationStart(Animation animation) {}
	
	private class SplashImagesAdapter extends FragmentStatePagerAdapter {

		private List<SlideFragment> slidesFragments;
		
		public SplashImagesAdapter(FragmentManager fm) {
			super(fm);
			this.slidesFragments = new ArrayList<SlideFragment>();
			for(int i = 0 ; i < imagesIds.length ; i++){
				SlideFragment fragment = new SlideFragment();
				fragment.setResourceId(imagesIds[i]);
				this.slidesFragments.add(fragment);
			}
		}

		@Override
		public Fragment getItem(int position) {
			return this.slidesFragments.get(position);
		}

		@Override
		public int getCount() {
			return this.slidesFragments.size();
		}
		
		public SlideFragment getFragment(int index){
			return this.slidesFragments.get(index);
		}
		
	}
	
	public static class SlideFragment extends Fragment {
		
		private int resourceId;
		private ImageView imageSlide;
		
		public void setResourceId(int resourceId){
			this.resourceId = resourceId;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.fragment_slide, container, false);
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			this.imageSlide = (ImageView) view.findViewById(R.id.img_slide);
			this.imageSlide.setImageResource(this.resourceId);
			Message.obtain(new Handler((Handler.Callback) this.getActivity()),
					MESSAGE_INITIATE).sendToTarget();
		}
		
		public void animateImage(Animation anim){
			if(this.imageSlide != null){
				imageSlide.startAnimation(anim);
			}
		}
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case MESSAGE_INITIATE:
			if(!this.isInitiated){
				this.isInitiated = true;
				this.startPagerAnimation();
			}
			break;
		case MESSAGE_SHOW_REGISTER:
			this.startActivity(new Intent(this, RegisterActivity.class));
			break;
		case MESSAGE_LOGGED:
			this.goToHomeScreen();
			break;
		}
		return true;
	}
	
	public void goToHomeScreen(){
		this.startActivity(new Intent(this, HomeActivity.class));
		this.finish();
	}
	
	public void onGooglePlusClicked(){
		this.onConnecting();
		if (!this.mPlusClient.isConnected()) {   
			this.mPlusClient.connect();
		} else {
			Person mPerson = this.mPlusClient.getCurrentPerson();
			this.onSocialNetworkLogin(new CreateUserRequest(
					mPerson.getName().getGivenName() + " " + mPerson.getName().getFamilyName(),
					this.mPlusClient.getAccountName(),
					mPerson.getDisplayName(),
					mPerson.getGender() == 1 ? "male" : "female",
					LoginType.GOOGLE_PLUS,
					mPerson.getBirthday()));
		}
	}
	
	private void onConnecting(){
		this.findViewById(R.id.social_network_options).setVisibility(View.GONE);
		this.findViewById(R.id.splash_progress).setVisibility(View.VISIBLE);
	}
	
	private void onConnectionIntentFinished(){
		this.findViewById(R.id.splash_progress).setVisibility(View.GONE);
		this.findViewById(R.id.social_network_options).setVisibility(View.VISIBLE);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Person mPerson = this.mPlusClient.getCurrentPerson();
		MMSPreferences.saveString(MMSPreferences.DISPLAY_IMG,
				"https://plus.google.com/s2/photos/profile/" + mPerson.getId());
		MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME,
				mPerson.getDisplayName());
		this.onSocialNetworkLogin(new CreateUserRequest(
				mPerson.getName().getGivenName() + " " + mPerson.getName().getFamilyName(),
				this.mPlusClient.getAccountName(),
				mPerson.getDisplayName(),
				mPerson.getGender() == 1 ? "female" : "male",
				LoginType.GOOGLE_PLUS,
				mPerson.getBirthday()));
	}
	
	private void onSocialNetworkLogin(BaseRequest request){
		new MMSAsyncRequest(this).execute(request);
	}

	@Override
	public void onDisconnected() {}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		super.onActivityResult(requestCode, responseCode, intent);
		this.mFbUiHelper.onActivityResult(requestCode, responseCode, intent);
		if(requestCode == this.REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK){
			this.mPlusClient.connect();
		} else {
			this.onConnectionIntentFinished();
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if(result.hasResolution()){
			try {
				result.startResolutionForResult(this, this.REQUEST_CODE_RESOLVE_ERR);
	        } catch (SendIntentException e) {
	        	this.mPlusClient.connect();
	        }
		} else {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
					this, this.REQUEST_CODE_RESOLVE_ERR).show();
			this.onConnectionIntentFinished();
		}
	}
	
	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		try {
			switch(response.getStatus()){
			case MMS_SUCCESS:
				this.onSuccessfulResponse(response);
				break;
			default:
				this.onErrorResponse(response);
				break;
			}
		} catch(Exception e){
			e.printStackTrace();
			this.onRequestFailed();
		}
	}

	protected void onSuccessfulResponse(MMSResponse response) {
		this.mmsApplication.setUser((MMSUser) response.getContent());
		MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME,
				this.mmsApplication.getUser().getName());
		this.goToHomeScreen();
	}
	
	protected void onRequestFailed() {
		this.onConnectionIntentFinished();
		if(this.mPlusClient.isConnected()){
			this.mPlusClient.disconnect();
		}
	}
	
	protected void onErrorResponse(MMSResponse response) {
		this.onConnectionIntentFinished();
		if(this.mPlusClient.isConnected()){
			this.mPlusClient.disconnect();
		}
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		this.onConnecting();
		if(session != null && session.isOpened()){
			Request.newMeRequest(session, new GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					MMSPreferences.saveString(MMSPreferences.DISPLAY_IMG,
							"http://graph.facebook.com/" + user.getId() + "/picture?type=large");
					MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME,
							user.getName());
					onSocialNetworkLogin(new CreateUserRequest(
							user.getName(),
							user.getProperty("email").toString(),
							user.getUsername(),
							user.getProperty("gender").toString(),
							LoginType.FACEBOOK,
							user.getBirthday()));
				}
			}).executeAsync();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.mFbUiHelper.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		this.mFbUiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(this.mFbUiHelper != null){
			this.mFbUiHelper.onDestroy();
		}
	}
	
	public void emptyMethod(View view){}
	
}
