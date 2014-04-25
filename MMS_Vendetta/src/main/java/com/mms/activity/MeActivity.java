package com.mms.activity;

import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.util.LoginType;
import com.mms.vendetta.R;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSResponse;
import com.mms.model.MMSUser;
import com.mms.request.GetUserRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;

public class MeActivity extends ActionBarActivity
	implements OnMMSRequestFinishedListener {

	private static final int CURRENT_RATING = 4;
	private static final String TAG_RATING_DETAILS_DIALOG = "rating_details_dialog";

	private MMSApplication mmsApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getSupportActionBar().hide();
		this.setContentView(R.layout.activity_me);
		this.mmsApplication = (MMSApplication) this.getApplication();
		if(this.mmsApplication.getUser() != null){
			this.setProfileFragment();
		} else {
			this.startUserRequest();
		}

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		try{
			switch(response.getStatus()){
			case MMS_SUCCESS:
				this.onSuccessfulResponse(response);
				break;
			case MMS_WRONG_COOKIE:
				this.onWrongCookie();
				break;
			default:
				this.onErrorResponse(response);
				break;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		this.findViewById(R.id.progress).setVisibility(View.GONE);
	}
	
	protected void onSuccessfulResponse(MMSResponse response) {
		this.mmsApplication.setUser((MMSUser) response.getContent());
		this.setProfileFragment();
	}
	
	protected void onWrongCookie(){
		Toast.makeText(this, R.string.error_wrong_cookie, Toast.LENGTH_SHORT).show();
		((MMSApplication) this.getApplication()).logout(this);
	}
	
	protected void onErrorResponse(MMSResponse response){
		Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
	}
	
	private void startUserRequest(){
		LoginType loginType = this.mmsApplication.loadLoginType();
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetUserRequest(cookie, loginType));
	}
	
	private void setProfileFragment(){
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.getSupportFragmentManager().beginTransaction()
			.replace(R.id.profile_content, new MeFragment())
			.commit();
	}
	
	public static class MeFragment extends Fragment {
		
		private ListView ratingList;
		
		private TextView welcomeMessage;
		private EditText nameField;
		private EditText usernameField;
		private EditText emailField;
		private EditText genderField;
		private EditText birthdayField;
		
		private MMSUser user;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			this.user = ((MMSApplication) this.getActivity().getApplication()).getUser();
			if(user.getGender().toLowerCase(Locale.ENGLISH).equals("male")){
				return inflater.inflate(R.layout.fragment_me_guy, container, false);
			} else {
				return inflater.inflate(R.layout.fragment_me_girl, container, false);
			}
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			this.welcomeMessage = (TextView) view.findViewById(R.id.welcome_message);
			this.nameField = (EditText) view.findViewById(R.id.edit_name);
			this.usernameField = (EditText) view.findViewById(R.id.edit_username);
			this.emailField = (EditText) view.findViewById(R.id.edit_email);
			this.genderField = (EditText) view.findViewById(R.id.edit_gender);
			this.birthdayField = (EditText) view.findViewById(R.id.edit_birthday);
			
			this.ratingList = (ListView) view.findViewById(R.id.rating_level);
			this.ratingList.setAdapter(new RatingLevelAdapter(this.getActivity()));
			this.ratingList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position,
						long viewId) {
					onRatingClicked(position);
				}
			});
			this.setProfileInfo();
		}
		
		private void setProfileInfo(){
			this.welcomeMessage.setText("Hi there, " + this.user.getAccount().getUserName() + "!");
			this.nameField.setHint(this.user.getName());
			this.usernameField.setHint(this.user.getAccount().getUserName());
			this.emailField.setHint(this.user.getAccount().getEmail());
			this.genderField.setHint(this.user.getGender());
			String birthday = this.user.getBirthday();
			if(birthday == null){
				this.birthdayField.setVisibility(View.GONE);
			} else {
				this.birthdayField.setHint(birthday);
			}
		}
		
		private void onRatingClicked(int position){
			FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
	    	Fragment prevDialog = this.getFragmentManager().findFragmentByTag(TAG_RATING_DETAILS_DIALOG);
	    
	    	if(prevDialog != null){
	    		transaction.remove(prevDialog);
	    	}
	    	transaction.commit();
	    	
			DialogFragment dialog = new RatingDetailsFragment();
			Bundle args = new Bundle();
			args.putInt("rating_selected", position);
			dialog.setArguments(args);
			dialog.show(this.getFragmentManager(), TAG_RATING_DETAILS_DIALOG);
		}
	}

	private static final class RatingLevelAdapter extends
			ArrayAdapter<RatingLevel> {

		public RatingLevelAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(this.getContext()).inflate(
					R.layout.list_item_rating_guy, parent, false);

			if (position < RatingLevel.getActualLevel(CURRENT_RATING)) {
				((ImageView) convertView.findViewById(R.id.rating_point))
						.setImageResource(R.drawable.ic_rating_blank);
			} else {
				((ImageView) convertView.findViewById(R.id.rating_point))
						.setImageResource(R.drawable.ic_rating_star);
			}

			if (position == RatingLevel.getActualLevel(CURRENT_RATING)) {
				convertView.findViewById(R.id.rating_title).setVisibility(
						View.VISIBLE);
				((TextView) convertView.findViewById(R.id.rating_title))
					.setText(RatingLevel.values()[RatingLevel.getActualLevel(CURRENT_RATING)].getLabel());
			}

			return convertView;
		}

		@Override
		public int getCount() {
			return RatingLevel.values().length;
		}

	}
	
	public static class RatingDetailsFragment extends DialogFragment {
		
		private int ratingSelected;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.news_dialog);
			this.ratingSelected = this.getArguments().getInt("rating_selected");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_rating_details, container, false);
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			((TextView) view.findViewById(R.id.rating_title)).setText(
					RatingLevel.values()[ratingSelected].getLabel());
		}
		
	}
	
	private enum RatingLevel {
		FAN_FROM_HELL("Fan from hell"), OBSESIVE("Obsesive"), GROUPIE("Groupie"), FAN(
				"Fan"), NEWBIE("Newbie");

		private String label;

		private RatingLevel(String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}
		
		public static int getActualLevel(int level){
			return RatingLevel.values().length - CURRENT_RATING;
		}

	}

}
