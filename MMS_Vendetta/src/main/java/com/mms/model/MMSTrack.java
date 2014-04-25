package com.mms.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MMSTrack implements MMSModel {

	private static final long serialVersionUID = -4139432505215117878L;
	
	private int id;
	private String name;
	private int trackNumber;
	private int artistId;
	private String duration;
	private String price;
	private int rating;
	private int albumId;
	private String demoUrl;
	private List<MMSUrl> urls;

	private MMSTrack(){}
	
	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}
	
	public int getTrackNumber(){
		return this.trackNumber;
	}
	
	private void setTrackNumber(int trackNumber){
		this.trackNumber = trackNumber;
	}

	public int getArtistId() {
		return artistId;
	}

	private void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public String getDuration() {
		return duration;
	}

	private void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPrice() {
		return price;
	}

	private void setPrice(String price) {
		this.price = price;
	}

	public int getRating() {
		return rating;
	}

	private void setRating(int rating) {
		this.rating = rating;
	}

	public int getAlbumId() {
		return albumId;
	}

	private void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	
	public String getDemoUrl(){
		return this.demoUrl;
	}
	
	private void setDemoUrl(String demoUrl){
		this.demoUrl = demoUrl;
	}
	
	public List<MMSUrl> getUrls(){
		return this.urls;
	}
	
	private void setUrls(List<MMSUrl> urls){
		this.urls = urls;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "trackId";
		private final String TAG_NAME = "name";
		private final String TAG_TRACK_NUMBER = "trackNumber";
		private final String TAG_ARTIST_ID = "artistId";
		private final String TAG_DURATION = "duration";
		private final String TAG_PRICE = "price";
		private final String TAG_RATING = "rating";
		private final String TAG_ALBUM_ID = "albumId";
		private final String TAG_DEMO_URL = "demoUrl";
		private final String TAG_URLS = "trackUrls";

		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonTrack = new JSONObject(input);
			MMSTrack track = new MMSTrack();
			
			track.setId(jsonTrack.optInt(TAG_ID));
			track.setName(jsonTrack.optString(TAG_NAME));
			track.setTrackNumber(jsonTrack.optInt(TAG_TRACK_NUMBER));
			track.setArtistId(jsonTrack.optInt(TAG_ARTIST_ID));
			track.setDuration(jsonTrack.optString(TAG_DURATION));
			track.setPrice(jsonTrack.optString(TAG_PRICE));
			track.setRating(jsonTrack.optInt(TAG_RATING));
			track.setAlbumId(jsonTrack.optInt(TAG_ALBUM_ID));
			track.setDemoUrl(jsonTrack.optString(TAG_DEMO_URL));
			
			JSONArray jsonUrls = jsonTrack.optJSONArray(TAG_URLS);
			List<MMSUrl> urls = new ArrayList<MMSUrl>();
			MMSUrl.Builder builder = new MMSUrl.Builder("");
			for(int i = 0 ; i < jsonUrls.length() ; i++){
				urls.add((MMSUrl) builder.build(jsonUrls.get(i).toString()));
			}
			track.setUrls(urls);
			
			return track;
		}

	}

}
