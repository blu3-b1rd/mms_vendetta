package com.mms.model;

import org.json.JSONObject;

public class MMSUrl implements MMSModel {

	private static final long serialVersionUID = -3186134970548197112L;

	private int id;
	private String url;
	private String urlType;
	
	private MMSUrl(){}
	
	public int getId(){
		return this.id;
	}
	
	private void setId(int id){
		this.id = id;
	}
	
	public String getUrl(){
		return this.url;
	}
	
	private void setUrl(String url){
		this.url = url;
	}
	
	public String getUrlType(){
		return this.urlType;
	}
	
	private void setUrlType(String urlType){
		this.urlType = urlType;
	}
	
	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "urlId";
		private final String TAG_URL = "url";
		private final String TAG_URL_TYPE = "urlType";
		
		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonUrl = new JSONObject(input);
			MMSUrl url = new MMSUrl();
			
			url.setId(jsonUrl.optInt(TAG_ID));
			url.setUrl(jsonUrl.optString(TAG_URL));
			url.setUrlType(jsonUrl.optString(TAG_URL_TYPE));
			
			return url;
		}
		
	}
	
}
