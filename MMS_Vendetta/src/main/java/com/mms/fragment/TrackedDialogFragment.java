package com.mms.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mms.app.MMSApplication;

public class TrackedDialogFragment extends DialogFragment {

	private Tracker tracker;
    private String screenName = "TrackedDialogFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.tracker = ((MMSApplication) this.getActivity().getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		this.tracker.setScreenName(this.screenName);
		this.tracker.send(new HitBuilders.AppViewBuilder().build());
	}
	
}
