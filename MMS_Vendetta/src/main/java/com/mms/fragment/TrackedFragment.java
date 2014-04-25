package com.mms.fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mms.app.MMSApplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class TrackedFragment extends Fragment {

    private Tracker tracker;
    private String screenName = "TrackedFragment";

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
