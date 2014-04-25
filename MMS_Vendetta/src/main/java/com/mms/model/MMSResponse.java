package com.mms.model;

import org.json.JSONObject;

import com.mms.request.BaseRequest.ResponseStatus;


public class MMSResponse implements MMSModel {

	private static final long serialVersionUID = 3869653641088303772L;
	
	private ResponseStatus status;
	private String message;
	private String type;
	private MMSModel content;

	private MMSResponse(){
		this.content = null;
	}
	
	public ResponseStatus getStatus() {
		return status;
	}

	private void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}

	public MMSModel getContent() {
		return content;
	}

	private void setContent(MMSModel content) {
		this.content = content;
	}

	public static class Builder extends MMSBuilder {

		private final String TAG_STATUS = "status";
		private final String TAG_MESSAGE = "message";
		private final String TAG_TYPE = "type";
		private final String TAG_CONTENT = "content";
		
		public Builder(String modelType) {
			super(modelType);
		}
		
		@Override
		public MMSModel build(String input) throws Exception {
			JSONObject jsonResponse = new JSONObject(input);
			MMSResponse response = new MMSResponse();
			
			response.setStatus(this.getLocalCode(jsonResponse.optInt(TAG_STATUS)));
			response.setMessage(jsonResponse.getString(TAG_MESSAGE));
			response.setType(jsonResponse.getString(TAG_TYPE));
			if(response.getStatus() == ResponseStatus.MMS_SUCCESS){
				response.setContent(MMSModelFactory.getFactory()
						.getModel(response.getType(), jsonResponse.getString(TAG_CONTENT)));
			}
			
			return response;
		}

	}

}
