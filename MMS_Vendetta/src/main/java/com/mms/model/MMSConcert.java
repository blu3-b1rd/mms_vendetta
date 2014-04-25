package com.mms.model;

import java.util.Date;

import org.json.JSONObject;

public class MMSConcert implements MMSModel {

	private static final long serialVersionUID = 7346512584868142508L;

	private String venue;
	private String address;
	private Date date;

	private MMSConcert() {
	}

	public String getVenue() {
		return venue;
	}

	private void setVenue(String venue) {
		this.venue = venue;
	}

	public String getAddress() {
		return address;
	}

	private void setAddress(String address) {
		this.address = address;
	}

	public Date getDate() {
		return date;
	}

	private void setDate(Date date) {
		this.date = date;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_VENUE = "venue";
		private final String TAG_ADDRESS = "address";
		private final String TAG_DATE = "date";
		
		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonConcert = new JSONObject(input);
			MMSConcert concert = new MMSConcert();
			
			concert.setVenue(jsonConcert.getString(TAG_VENUE));
			concert.setAddress(jsonConcert.getString(TAG_ADDRESS));
			concert.setDate(this.dateFromString(jsonConcert.getString(TAG_DATE)));
			
			return concert;
		}

	}

}
