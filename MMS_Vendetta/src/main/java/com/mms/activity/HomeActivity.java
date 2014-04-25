package com.mms.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.app.MMSApplication;
import com.mms.util.MMSUtils;
import com.mms.vendetta.R;

public class HomeActivity extends ActionBarActivity
	implements DrawerActivity, OnNavigationListener {

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState == null){
			this.overridePendingTransition(R.anim.fade_in, 0);
		}
		this.setContentView(R.layout.activity_news);
		this.configure();
		this.configureActionBar();

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.drawerToggle.syncState();
	}

	private void configure() {
		this.drawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
		this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout,
				R.drawable.ic_drawer, 0, 0){
			@Override
			public void onDrawerClosed(View drawerView) {
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				updateActionbarAlpha(slideOffset);
			}
		};
		this.drawerLayout.setDrawerListener(this.drawerToggle);
	}
	
	private void updateActionbarAlpha(float factor){
		this.getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(MMSUtils.colorWithAlpha(
						0xffeeeeee, factor)));
	}
	
	private void configureActionBar(){
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setHomeButtonEnabled(true);
		actionbar.setBackgroundDrawable(new ColorDrawable(
				MMSUtils.colorWithAlpha(0xffeeeeee, 0.0f)));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(this.drawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
		switch(item.getItemId()){
		case R.id.menu_logout:
			((MMSApplication) this.getApplication()).logout(this);
			return true;
		default:	
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void hideDrawer(){
		this.drawerLayout.closeDrawer(GravityCompat.START);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {
		return false;
	}

}
