package com.mms.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.mms.request.BaseRequest.ResponseStatus;

public interface MMSModel extends Serializable {
	
	public abstract class MMSBuilder {

		// "date": "17/05/2013 04:27:08 p.m."
		private static final String API_DATE_FORMAT = "MM/dd/yyyy";
		
		protected String modelType;
		
		public MMSBuilder(String modelType){
			this.modelType = modelType.replace("list", "");
		}
		
		protected Date dateFromString(String stringDate, String dateFormat){
			DateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
			try {
				return format.parse(stringDate);
			} catch (Exception e) {
				e.printStackTrace();
				return new Date();
			}
		}
		
		protected Date dateFromString(String stringDate){
			return this.dateFromString(stringDate, API_DATE_FORMAT);
		}
		
		protected ResponseStatus getLocalCode(int status){
			switch(status){
			case 200:
				return ResponseStatus.MMS_SUCCESS;
			case 408:
				return ResponseStatus.MMS_WRONG_COOKIE;
			default:
				return ResponseStatus.MMS_ERROR;
			}
		}
		
		public abstract MMSModel build(String input) throws Exception;
	}

}
