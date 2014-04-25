package com.mms.fragment;

import com.mms.app.MMSPreferences;
import com.mms.model.MMSResponse;
import com.mms.request.GetYouTubePlaylistRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.vendetta.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VideosFragment extends Fragment
	implements OnMMSRequestFinishedListener {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_videos, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetYouTubePlaylistRequest(cookie));
	}

	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		// TODO Auto-generated method stub
		
	}
	
}

