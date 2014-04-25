package com.mms.activity;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.app.MMSApplication;
import com.mms.vendetta.R;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class EventsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_events);
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setTitle(R.string.events_title);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
