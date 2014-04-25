package com.mms.model;

import org.json.JSONObject;

public class MMSPlaylist implements MMSModel {

	private static final long serialVersionUID = 5873118148831168760L;

	private int id;
	private String name;
	private String youtubeId;
	private MMSUrl url;
	
	private MMSPlaylist(){}

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

	public String getYoutubeId() {
		return youtubeId;
	}

	private void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public MMSUrl getUrl() {
		return url;
	}

	private void setUrl(MMSUrl url) {
		this.url = url;
	}
	
	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "playlistId";
		private final String TAG_NAME = "name";
		private final String TAG_YOUTUBE_ID = "youtubeId";
		private final String TAG_URL = "url";
		
		public Builder(String modelType){
			super(modelType);
		}
		
		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonPlaylist = new JSONObject(input);
			MMSPlaylist playlist = new MMSPlaylist();
			
			playlist.setId(jsonPlaylist.optInt(TAG_ID));
			playlist.setName(jsonPlaylist.optString(TAG_NAME));
			playlist.setYoutubeId(jsonPlaylist.optString(TAG_YOUTUBE_ID));
			
			MMSUrl.Builder urlBuilder = new MMSUrl.Builder("");
			playlist.setUrl((MMSUrl) urlBuilder.build(
					jsonPlaylist.optString(TAG_URL)));
			
			return playlist;
		}
		
	}
	
}
