package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetEventsRequest extends BaseRequest {

	private static final String PARAM_COOKIE = "cookie";
	
	public GetEventsRequest(String cookie) {
		super("artist/concerts", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
	}

}
