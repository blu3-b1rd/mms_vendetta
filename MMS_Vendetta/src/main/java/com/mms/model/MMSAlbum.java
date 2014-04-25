package com.mms.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class MMSAlbum implements MMSModel {

	private static final long serialVersionUID = -243364530436613725L;
	
	private int id;
	private String name;
	private Date releaseDate;
	private int numberOfTracks;
	private String coverUrl;
	private String label;
	private int rating;
	private List<MMSUrl> urls;
	
	private MMSAlbum() {
	}

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

	public Date getReleaseDate() {
		return releaseDate;
	}

	private void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getNumberOfTracks() {
		return numberOfTracks;
	}

	private void setNumberOfTracks(int numberOfTracks) {
		this.numberOfTracks = numberOfTracks;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	private void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getLabel() {
		return label;
	}

	private void setLabel(String label) {
		this.label = label;
	}

	public int getRating() {
		return rating;
	}

	private void setRating(int rating) {
		this.rating = rating;
	}
	
	public List<MMSUrl> getUrls(){
		return this.urls;
	}
	
	private void setUrls(List<MMSUrl> urls){
		this.urls = urls;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "albumId";
		private final String TAG_NAME = "name";
		private final String TAG_RELEASE_DATE = "releaseDate";
		private final String TAG_NUMBER_OF_TRACKS = "numberOfTracks";
		private final String TAG_ALBUM_COVER = "coverImage";
		private final String TAG_LABEL = "label";
		private final String TAG_RATING = "rating";
		private final String TAG_URLS = "albumUrls";
		
		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonAlbum = new JSONObject(input);
			MMSAlbum album = new MMSAlbum();
			
			album.setId(jsonAlbum.getInt(TAG_ID));
			album.setName(jsonAlbum.getString(TAG_NAME));
			album.setReleaseDate(this.dateFromString(jsonAlbum.getString(TAG_RELEASE_DATE)));
			album.setNumberOfTracks(jsonAlbum.getInt(TAG_NUMBER_OF_TRACKS));
			album.setCoverUrl(jsonAlbum.getString(TAG_ALBUM_COVER));
			album.setLabel(jsonAlbum.getString(TAG_LABEL));
			album.setRating(jsonAlbum.getInt(TAG_RATING));
			
			List<MMSUrl> urls = new ArrayList<MMSUrl>();
			JSONArray jsonUrls = jsonAlbum.optJSONArray(TAG_URLS);
			MMSUrl.Builder builder = new MMSUrl.Builder("");
			for(int i = 0 ; i < jsonUrls.length() ; i++){
				urls.add((MMSUrl) builder.build(
						jsonUrls.get(i).toString()));
			}
			album.setUrls(urls);
			
			return album;
		}

	}

}
