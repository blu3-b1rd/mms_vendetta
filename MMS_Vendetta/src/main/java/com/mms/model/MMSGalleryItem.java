package com.mms.model;

import org.json.JSONObject;

public class MMSGalleryItem implements MMSModel {

	private static final long serialVersionUID = -5683975586494133361L;
	
	private int id;
	private int urlId;
	private int urlType;
	private String url;
	private String description;
	
	private MMSGalleryItem(){}

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public int getUrlId() {
		return urlId;
	}

	private void setUrlId(int urlId) {
		this.urlId = urlId;
	}

	public int getUrlType() {
		return urlType;
	}

	private void setUrlType(int urlType) {
		this.urlType = urlType;
	}

	public String getUrl() {
		return url;
	}

	private void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}
	
	public static class Builder extends MMSBuilder {
		
		private final String TAG_ID = "imageId";
		private final String TAG_IMAGE_DESCRIPTION = "imageDescription";
		private final String TAG_URL_ID = "urlId";
		private final String TAG_URL_TYPE = "urlType";
		private final String TAG_IMAGE_URL = "imageUrl";
		
		public Builder(String modelType){
			super(modelType);
		}
		
		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonGalleryItem = new JSONObject(input);
			MMSGalleryItem item = new MMSGalleryItem();
			
			item.setId(jsonGalleryItem.getInt(TAG_ID));
			item.setDescription(jsonGalleryItem.getString(TAG_IMAGE_DESCRIPTION));
			item.setUrlId(jsonGalleryItem.getInt(TAG_URL_ID));
			item.setUrlType(jsonGalleryItem.getInt(TAG_URL_TYPE));
			item.setUrl(jsonGalleryItem.getString(TAG_IMAGE_URL));
			
			return item;
		}
		
	}

}
