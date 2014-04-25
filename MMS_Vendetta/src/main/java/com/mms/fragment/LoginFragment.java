package com.mms.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mms.activity.SplashActivity;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSResponse;
import com.mms.model.MMSUser;
import com.mms.request.BaseRequest.ResponseStatus;
import com.mms.request.GetUserRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.util.LoginType;
import com.mms.vendetta.R;

public class LoginFragment extends TrackedFragment
	implements OnClickListener, OnMMSRequestFinishedListener {
	
	private EditText emailField;
	private EditText passwordField;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.emailField = (EditText) view.findViewById(R.id.edit_email);
		this.passwordField = (EditText) view.findViewById(R.id.password_field);
		((Button) view.findViewById(R.id.button_show_login)).setOnClickListener(this);
		((Button) view.findViewById(R.id.button_login)).setOnClickListener(this);
		((Button) view.findViewById(R.id.button_register)).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_show_login:
			this.onShowLoginClicked();
			break;
		case R.id.button_login:
			this.onLoginClicked();
			break;
		case R.id.button_register:
			this.onRegisterClicked();
			break;
		default:
			break;
		}
	}
	
	private void onShowLoginClicked(){
		this.getView().findViewById(R.id.button_show_login).setVisibility(View.GONE);
		this.getView().findViewById(R.id.button_login).setVisibility(View.VISIBLE);
		this.getView().findViewById(R.id.form_login).setVisibility(View.VISIBLE);
	}
	
	private void onLoginClicked(){
		if(this.validate()){
			this.getView().findViewById(R.id.login_content).setVisibility(View.GONE);
			this.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
			new MMSAsyncRequest(this).execute(new GetUserRequest(LoginType.EMAIL, 
					this.emailField.getText().toString(),
					this.passwordField.getText().toString()));
		} else {
			Toast.makeText(this.getActivity(), R.string.error_verify_data,
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean validate(){
		boolean flag = true;
		
		if(this.emailField.getText().toString().isEmpty()){
			this.highlight(this.emailField);
			flag = false;
		}
		
		if(this.passwordField.getText().toString().isEmpty()){
			this.highlight(this.passwordField);
			flag = false;
		}
		
		return flag;
	}
	
	private void highlight(View view){
		view.setBackgroundColor(Color.parseColor("#55DD1E2F"));
	}
	
	private void onRegisterClicked(){
		Message.obtain(new Handler((Handler.Callback) this.getActivity()),
				SplashActivity.MESSAGE_SHOW_REGISTER).sendToTarget();
	}

	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		if(response == null){
			Toast.makeText(this.getActivity(), R.string.error_connection_failed, Toast.LENGTH_SHORT).show();
			this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
			this.getView().findViewById(R.id.login_content).setVisibility(View.VISIBLE);
		} else if(response.getStatus() == ResponseStatus.MMS_SUCCESS){
			MMSApplication app = (MMSApplication) this.getActivity().getApplication(); 
			app.setUser((MMSUser) response.getContent());
			MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME,
					app.getUser().getName());
			Message.obtain(new Handler((Handler.Callback) this.getActivity()),
					SplashActivity.MESSAGE_LOGGED).sendToTarget();
		} else {
			Toast.makeText(this.getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();
			this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
			this.getView().findViewById(R.id.login_content).setVisibility(View.VISIBLE);
		}
	}
	
}
