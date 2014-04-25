package com.mms.model;

import java.util.Date;

import org.json.JSONObject;

public class MMSNewsEntry implements MMSModel {

	private static final long serialVersionUID = 6829137439194791315L;

	private int id;
	private String title;
	private String content;
	private Date date;
	
	private MMSNewsEntry(){}

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	private void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	private void setDate(Date date) {
		this.date = date;
	}
	
	public static class Builder extends MMSBuilder {

		private final String TAG_ID = "newsId";
		private final String TAG_TITLE = "title";
		private final String TAG_CONTENT = "content";
		private final String TAG_DATE = "date";
		
		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonEntry = new JSONObject(input);
			MMSNewsEntry entry = new MMSNewsEntry();
			
			entry.setId(jsonEntry.getInt(TAG_ID));
			entry.setTitle(jsonEntry.getString(TAG_TITLE));
			entry.setContent(jsonEntry.getString(TAG_CONTENT));
			entry.setDate(this.dateFromString(jsonEntry.getString(TAG_DATE)));
			
			return entry;
		}
		
	}

}
