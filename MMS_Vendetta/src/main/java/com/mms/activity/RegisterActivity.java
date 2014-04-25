package com.mms.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.util.LoginType;
import com.mms.vendetta.R;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSResponse;
import com.mms.model.MMSUser;
import com.mms.request.CreateUserRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;

public class RegisterActivity extends ActionBarActivity
	implements OnMMSRequestFinishedListener, DatePickerDialog.OnDateSetListener {

	private TextView nameField;
	private TextView birthdayField;
	private Spinner genderField;
	private TextView usernameField;
	private TextView emailField;
	private TextView passwordField;
	private TextView confirmPasswordField;
	private Calendar selectedDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getSupportActionBar().hide();
		this.overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
		this.setContentView(R.layout.activity_register);
		this.init();

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}

	private void init(){
		this.nameField = (TextView) this.findViewById(R.id.edit_name);
		this.birthdayField = (Button) this.findViewById(R.id.edit_birthday);
		this.genderField = (Spinner) this.findViewById(R.id.edit_gender);
		this.usernameField = (TextView) this.findViewById(R.id.edit_username);
		this.emailField = (TextView) this.findViewById(R.id.edit_email);
		this.passwordField = (TextView) this.findViewById(R.id.password_field);
		this.confirmPasswordField = (TextView) this.findViewById(R.id.password_conf_field);
		this.selectedDate = Calendar.getInstance();
		this.setDateOnButton();
	}

	public void onDateClicked(View view) {
		int year = this.selectedDate.get(Calendar.YEAR);
		int month = this.selectedDate.get(Calendar.MONTH);
		int day = this.selectedDate.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, this, year, month, day).show();
	}
	
	public void onDateSet(DatePicker view, int year, int month, int day) {
		this.selectedDate.set(year, month, day);
		this.setDateOnButton();
	}
	
	private void setDateOnButton(){
		int year = this.selectedDate.get(Calendar.YEAR);
		int day = this.selectedDate.get(Calendar.DAY_OF_MONTH);

		this.birthdayField.setText(new StringBuilder("")
		.append(year).append(" / ")
		.append(this.selectedDate.getDisplayName(Calendar.MONTH,
				Calendar.LONG, Locale.getDefault())).append(" / ")
		.append(day).toString());
	}

	public void onRegisterClicked(View view) {
		if (this.validateData()) {
			this.findViewById(R.id.button_register).setEnabled(false);
			this.findViewById(R.id.register_form_container).setVisibility(
					View.GONE);
			this.findViewById(R.id.progress).setVisibility(View.VISIBLE);
			new MMSAsyncRequest(this).execute(new CreateUserRequest(
					this.nameField.getText().toString(),
					this.emailField.getText().toString(),
					this.usernameField.getText().toString(),
					this.genderField.getSelectedItem().toString(),
					LoginType.EMAIL,
					this.getSelectedDateString(),
					this.passwordField.getText().toString()));
		} else {
			Toast.makeText(this, R.string.error_verify_data, Toast.LENGTH_SHORT).show();
		}
	}
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private String getSelectedDateString(){
		SimpleDateFormat dateFormater = new SimpleDateFormat(
				DATE_FORMAT, Locale.getDefault());
		return dateFormater.format(this.selectedDate.getTime());
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
	
	protected void onRequestFailed() {
		this.findViewById(R.id.button_register).setEnabled(true);
		this.findViewById(R.id.register_form_container).setVisibility(
				View.VISIBLE);
		this.findViewById(R.id.progress).setVisibility(View.GONE);
	}
	
	protected void onSuccessfulResponse(MMSResponse response) {
		MMSApplication app = (MMSApplication) this.getApplication();
		app.setUser((MMSUser) response.getContent());
		MMSPreferences.saveString(MMSPreferences.DISPLAY_NAME, 
				app.getUser().getName());
		Toast.makeText(this, R.string.notice_register_successful, Toast.LENGTH_SHORT).show();
		this.finish();
	}
	
	protected void onErrorResponse(MMSResponse response) {
		this.findViewById(R.id.button_register).setEnabled(true);
		this.findViewById(R.id.register_form_container).setVisibility(
				View.VISIBLE);
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		Toast.makeText(this, response.getMessage(), Toast.LENGTH_LONG).show();
	}

	private boolean validateData() {
		boolean flag = true;
		
		if(this.nameField.getText().toString().isEmpty()){
			this.highlight(this.nameField);
			flag = false;
		}
		if(this.usernameField.getText().toString().isEmpty()){
			this.highlight(this.usernameField);
			flag = false;
		}
		if(this.emailField.getText().toString().isEmpty()){
			this.highlight(this.emailField);
			flag = false;
		}
		if(this.passwordField.getText().toString().isEmpty()){
			this.highlight(this.passwordField);
			flag = false;
		}
		if(this.confirmPasswordField.getText().toString().isEmpty()){
			this.highlight(this.confirmPasswordField);
			flag = false;
		}
		if(!this.passwordField.getText().toString()
				.equals(this.confirmPasswordField.getText().toString())){
			this.highlight(this.passwordField);
			this.highlight(this.confirmPasswordField);
			flag = false;
		}
		if(!this.validBirthDate()){
			Toast.makeText(this, R.string.error_invalid_date, Toast.LENGTH_LONG).show();
			flag = false;
		}
		
		return flag;
	}
	
	private boolean validBirthDate(){		
		Date current = new Date();
		long milisDiff = current.getTime() - this.selectedDate.getTimeInMillis();
		if(milisDiff < 0){ return false; }
		
		long yearMilis = 1000L * 60 * 60 * 24 * 365;
		int yearsDiff = (int) (milisDiff / yearMilis);
		
		if(yearsDiff < 10){
			return false;
		}
		
		return true;
	}

	private void highlight(View view) {
		view.setBackgroundColor(Color.parseColor("#11DD1E2F"));
	}

}
