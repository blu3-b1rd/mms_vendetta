package com.mms.util;

import java.util.Locale;

import android.app.Activity;

import com.mms.activity.DiscographyActivity;
import com.mms.activity.EventsActivity;
import com.mms.activity.GalleryActivity;
import com.mms.activity.VideosActivity;
import com.mms.vendetta.R;

public enum HomeSection {
	
	//ME(R.drawable.ic_action_me, "Me", MeSectionActivity.class),
	EVENTS(R.drawable.ic_action_events, R.string.events_title,
			EventsActivity.class),
	VIDEOS(R.drawable.ic_action_events, R.string.videos_title,
			VideosActivity.class),
	DISCOGRAPHY(R.drawable.ic_action_discography, R.string.discography_title,
			DiscographyActivity.class),
	GALLERY(R.drawable.ic_action_gallery, R.string.gallery_title,
			GalleryActivity.class);
	//ABOUT(R.drawable.ic_action_about, "About", GallerySectionActivity.class);
	//YOUTUBE(R.drawable.ic_action_youtube, "Our Videos", YoutubeVideosFragment.class);
	
	private String fragmentTag;
	private int iconId;
	private int title;
	private Class<? extends Activity> activityClass;
	
	private HomeSection(int iconId, int name, Class<? extends Activity> activityClass){
		this.iconId = iconId;
		this.title = name;
		this.activityClass = activityClass;
		this.fragmentTag = this.toString().toLowerCase(Locale.ENGLISH) + "_fragment";
	}

	public String getFragmentTag(){
		return this.fragmentTag;
	}
	
	public int getIconId(){
		return this.iconId;
	}
	
	public int getTitle(){
		return this.title;
	}
	
	public Class<? extends Activity> getActivityClass(){
		return this.activityClass;
	}
	
}
