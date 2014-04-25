package com.mms.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.app.MMSApplication;
import com.mms.fragment.AlbumFragment;
import com.mms.model.MMSAlbum;
import com.mms.model.MMSUrl;
import com.mms.vendetta.R;

public class AlbumActivity extends ActionBarActivity {

	public static final String META_ALBUM = "meta_album";
	
	private MMSAlbum mAlbum;
	private Map<Integer, String> urlsMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_album);
		this.mAlbum = (MMSAlbum) this.getIntent().getExtras().getSerializable(META_ALBUM);
		this.configure();

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}
	
	private void configure(){
		this.configureActionBar();
		
		AlbumFragment albumFragment = new AlbumFragment();
		albumFragment.setArguments(this.getIntent().getExtras());
		
		FragmentTransaction ft = this.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.layout_album, albumFragment).commit();
	}
	
	private void configureActionBar(){
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setTitle(this.mAlbum.getName());
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
	}
	
	@SuppressLint("UseSparseArrays")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.urlsMap = new HashMap<Integer, String>();
		
		for(MMSUrl mUrl : this.mAlbum.getUrls()){
			menu.add(Menu.NONE, mUrl.getId(), Menu.NONE, mUrl.getUrlType());
			this.urlsMap.put(mUrl.getId(), mUrl.getUrl());
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			return true;
		}
		
		if(!this.urlsMap.containsKey(item.getItemId())){
			return super.onOptionsItemSelected(item);
		}
		
		Intent urlIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(this.urlsMap.get(item.getItemId())));
		this.startActivity(urlIntent);
		
		return true;
	}

}
